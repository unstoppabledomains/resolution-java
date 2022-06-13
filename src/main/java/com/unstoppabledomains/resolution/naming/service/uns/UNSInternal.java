
package com.unstoppabledomains.resolution.naming.service.uns;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.unstoppabledomains.config.KnownRecords;
import com.unstoppabledomains.config.network.model.Location;
import com.unstoppabledomains.exceptions.ContractCallException;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.Namehash;
import com.unstoppabledomains.resolution.TokenUriMetadata;
import com.unstoppabledomains.resolution.contracts.JsonProvider;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.contracts.uns.ProxyData;
import com.unstoppabledomains.resolution.contracts.uns.ProxyReader;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.dns.DnsUtils;
import com.unstoppabledomains.resolution.naming.service.BaseNamingService;
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.util.Utilities;

class UNSInternal extends BaseNamingService {
  private final ProxyReader proxyReaderContract;
  private UNSLocation location;
  
  UNSInternal(UNSLocation location, NSConfig config, IProvider provider) {
    super(config, provider);
    this.location = location;
    String proxyReaderAddress = config.getContractAddress();
    this.proxyReaderContract = new ProxyReader(config.getBlockchainProviderUrl(), proxyReaderAddress, provider);
  }

  @Override
  public NamingServiceType getType() {
    return NamingServiceType.UNS;
  }

  public Boolean isSupported(String domain) throws NamingServiceException {
    String[] split = domain.split("\\.");
    BigInteger tokenID;
    try {
      tokenID = getTokenID(split[split.length - 1]);
    } catch (NamingServiceException e) {
      return false;
    }
    return proxyReaderContract.getExists(tokenID);
  }

  @Override
  public Map<String, String> getAllRecords(String domain) throws NamingServiceException {
    BigInteger tokenID = getTokenID(domain);
    Map<String, String> metadataRecords = new HashMap<>();
    try {
      metadataRecords = getTokenUriMetadata(tokenID).getProperties().getRecords();
    } catch(NamingServiceException e) {
      if (e.getCode() == NSExceptionCode.UnregisteredDomain) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
      }
      throw e;
    }
    Set<String> recordsSet = Utilities.combineTwoSets(KnownRecords.getAllRecordKeys(), metadataRecords.keySet());
    String[] records = recordsSet.stream().toArray(String[] ::new);
    ProxyData data = resolveKeys(records, domain);
    List<String> values = data.getValues();

    Map<String, String> result = new HashMap<>();
    Utilities.iterateSimultaneously(Arrays.asList(records), values, (record, value) -> {
      if (!value.isEmpty()) {
        result.put(record, value);
      }
    });
    return result;
  }

  @Override
  public String getRecord(String domain, String recordKey) throws NamingServiceException {
    if (recordKey.equals("dweb.ipfs.hash") || recordKey.equals("ipfs.html.value")) {
      return getIpfsHash(domain);
    }
    ProxyData data = resolveKey(recordKey, domain);
    checkDomainOwnership(data, domain);
    String result = data.getValues().get(0);
    if (Utilities.isEmptyResponse(result)) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d|r|l", domain, recordKey, location.getName()));
    }
    return result;
  }

  @Override
  public Map<String, String> getRecords(String domain, List<String> recordsKeys) throws NamingServiceException {
    ProxyData data = resolveKeys(recordsKeys.toArray(new String[recordsKeys.size()]), domain);
    List<String> values = data.getValues();
    Map<String, String> result = IntStream.range(0, recordsKeys.size())
    .boxed()
    .collect(Collectors.toMap(recordsKeys::get, values::get));
    return result;
  }

  @Override
  public  String getOwner(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = getTokenID(domain);
      String owner = owner(tokenID);
      if (Utilities.isEmptyResponse(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain,
          new NSExceptionParams("d|n|l", domain, "UNS", location.getName()));
      }
      return owner;
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("d|n|l", domain, "UNS", location.getName()));
    }
  }

  @Override
  public Map<String, String> batchOwners(List<String> domains) throws NamingServiceException {
    Map<String, String> domainOwnerMap = new HashMap<>(domains.size());
    try {
      List<BigInteger> tokenIDs = new ArrayList<>();
      for (String domain: domains) {
        tokenIDs.add(getTokenID(domain));
      }
      
      List<String> rawOwners = proxyReaderContract.batchOwners(tokenIDs.toArray(new BigInteger[tokenIDs.size()]));
      Utilities.iterateSimultaneously(domains, rawOwners, (domain, rawOwner) -> {
        String owner = Utilities.isEmptyResponse(rawOwner) ? null : rawOwner;
        domainOwnerMap.put(domain, owner);
      });
      
      return domainOwnerMap;
    } catch(Exception e) {
      throw configureNamingServiceException(e,
        new NSExceptionParams("d|n|l", String.join(", ", domains),"UNS", location.getName()));
    }
  }

  @Override
  public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException {
    DnsUtils util = new DnsUtils();
    List<String> keys = constructDnsRecords(types);
    ProxyData data = resolveKeys(keys.toArray(new String[keys.size()]), domain);
    List<String> values = data.getValues();
    Map<String, String> rawData = new HashMap<>();
    for (int i = 0; i < values.size(); i++) {
      rawData.put(keys.get(i), values.get(i));
    }
    return util.toList(rawData);
  }

  @Override
  public String getTokenUri(BigInteger tokenID) throws NamingServiceException {
    try {
      String tokenURI = proxyReaderContract.getTokenUri(tokenID);
      if (tokenURI == null || tokenURI.isEmpty()) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n|l", "getTokenUri", "UNS", location.getName()));
      }
      return tokenURI;
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("m|n|l", "getTokenUri", "UNS", location.getName()));
    }
  }

  @Override
  public String getDomainName(BigInteger tokenID) throws NamingServiceException {
      TokenUriMetadata metadata = getTokenUriMetadata(tokenID);
      String domainName = metadata.getName();
      if (domainName == null) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n|l", "getDomainName", "UNS", location.getName()));
      }
      return domainName;
  }

  @Override
  public Map<String, Location> getLocations(String... domains) throws NamingServiceException {
    Map<String, Location> locations = new HashMap<>(domains.length);
    try {
      BigInteger[] tokenIDs = new BigInteger[domains.length];
      for (int i = 0; i < domains.length; i++) {
        tokenIDs[i] = getTokenID(domains[i]);
      }

      List<Location.LocationBuilder> results = proxyReaderContract.getLocationAddresses(tokenIDs);
      for (int i = 0; i < domains.length; i++) {
        Location.LocationBuilder builder = results.get(i);
        Location location = null;
        if (builder != null) {
          builder.Blockchain(this.location.getBlockchain());
          builder.BlockchainProviderURL(this.getProviderUrl());
          builder.NetworkId(this.getNetwork());
          location = builder.build();
        }
        locations.put(domains[i], location);
      }
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("m|n|l", "getLocations", "UNS", location.getName()));
    }
    return locations;
  }

  @Override
  public String getReverseTokenId(String address) throws NamingServiceException {
      BigInteger tokenId = proxyReaderContract.getReverseResolution(address);
      if (tokenId.equals(BigInteger.ZERO)) {
          throw new NamingServiceException(NSExceptionCode.ReverseResolutionNotSpecified, 
          new NSExceptionParams("m|n|l|a", "getReverseTokenId", "UNS", location.getName(), address));
      }
      return Utilities.tokenIDToNamehash(tokenId);
  }

  protected  ProxyData resolveKey(String key, String domain) throws NamingServiceException {
    return resolveKeys(new String[]{key}, domain);
  }

  protected ProxyData resolveKeys(String[] keys, String domain) throws NamingServiceException {
    BigInteger tokenID = getTokenID(domain);
    ProxyData data =  proxyReaderContract.getProxyData(keys, tokenID);
    checkDomainOwnership(data, domain);
    return data;
  }

  private  String getIpfsHash(String domain) throws NamingServiceException {
    String[] keys = {"dweb.ipfs.hash", "ipfs.html.value"};
    ProxyData data = resolveKeys(keys, domain);

    List<String> values = data.getValues();
    if (values.get(0).isEmpty() && values.get(1).isEmpty()) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
              new NSExceptionParams("d|r|l", domain, keys[0], location.getName()));
    }
    return values.get(0).isEmpty() ? values.get(1) : values.get(0);
  }

  private List<String> constructDnsRecords(List<DnsRecordsType> types) {
    List<String> records = new ArrayList<>();
    records.add("dns.ttl");
    for (DnsRecordsType type: types) {
      records.add("dns." + type.toString());
      records.add("dns." + type.toString() + ".ttl");
    }
    return records;
  }

  private TokenUriMetadata getTokenUriMetadata(BigInteger tokenID) throws NamingServiceException {
    try {
      String tokenURI = this.getTokenUri(tokenID);
      JsonProvider provider = new JsonProvider();
      TokenUriMetadata metadata = provider.request(tokenURI, TokenUriMetadata.class);
      return metadata;
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("m|n|l", "getTokenUriMetadata", "UNS", location.getName()));
    }
  }

  private void checkDomainOwnership(ProxyData data, String domain) throws NamingServiceException {
    if (data.getResolver().isEmpty()) {
      if (data.getOwner().isEmpty()) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, 
          new NSExceptionParams("d|l", domain, location.getName()));
      }
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver,
      new NSExceptionParams("d|l", domain, location.getName()));
    }
  }

  private NamingServiceException configureNamingServiceException(Exception e, NSExceptionParams params) {
    if (e instanceof NamingServiceException) {
      return (NamingServiceException) e;
    }
    if (e instanceof UnknownHostException) {
      return new NamingServiceException(NSExceptionCode.BlockchainIsDown, params, e);
    } else if (e instanceof ContractCallException) {
      return new NamingServiceException(NSExceptionCode.RecordNotFound, params, e);
    }
    return new NamingServiceException(NSExceptionCode.UnknownError, params, e);
  }

  private String owner(BigInteger tokenID) {
    return proxyReaderContract.getOwner(tokenID);
  }

  private BigInteger getTokenID(String domain) throws NamingServiceException {
    String hash = getNamehash(domain);
    return new BigInteger(hash.substring(2), 16);
  }

  @Override
  public String getNamehash(String domain) throws NamingServiceException {
    return Namehash.nameHash(domain);
  }
}
