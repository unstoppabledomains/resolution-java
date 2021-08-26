
package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.exceptions.ContractCallException;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.Namehash;
import com.unstoppabledomains.resolution.contracts.uns.ProxyData;
import com.unstoppabledomains.resolution.contracts.uns.ProxyReader;
import com.unstoppabledomains.resolution.contracts.uns.Registry;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.dns.DnsUtils;
import com.unstoppabledomains.util.Utilities;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UNS extends BaseNamingService {
  private final ProxyReader proxyReaderContract;
  
  public UNS(NSConfig config, IProvider provider) {
    super(config, provider);
    String proxyReaderAddress = config.getContractAddress();
    this.proxyReaderContract = new ProxyReader(config.getBlockchainProviderUrl(), proxyReaderAddress, provider);
  }

  @Override
  public NamingServiceType getType() {
    return NamingServiceType.UNS;
  }

  public Boolean isSupported(String domain) throws NamingServiceException {
    String[] split = domain.split("\\.");
    if (split.length == 0 || split[split.length - 1].equals("zil")) {
      return false;
    }
    BigInteger tokenID;
    try {
      tokenID = getTokenID(split[split.length - 1]);
    } catch (NamingServiceException e) {
      return false;
    }
    return proxyReaderContract.getExists(tokenID);
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
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d|r", domain, recordKey));
    }
    return result;
  }

  @Override
  public  String getOwner(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = getTokenID(domain);
      String owner = owner(tokenID);
      if (Utilities.isEmptyResponse(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain,
          new NSExceptionParams("d|n", domain, "UNS"));
      }
      return owner;
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("d|n", domain, "UNS"));
    }
  }

  @Override
  public String[] batchOwners(String[] domains) throws NamingServiceException {
    try {
      BigInteger[] tokenIDs = new BigInteger[domains.length];
      for (int i = 0; i < domains.length; i++) {
        tokenIDs[i] = getTokenID(domains[i]);
      }
      String[] rawOwners = proxyReaderContract.batchOwners(tokenIDs);
      String[] owners = new String[rawOwners.length];
      for (int i = 0; i < rawOwners.length; i++) {
        owners[i] = Utilities.isEmptyResponse(rawOwners[i]) ? null : rawOwners[i];
      }
      return owners;
    } catch(Exception e) {
      throw configureNamingServiceException(e,
        new NSExceptionParams("d|n", String.join(", ", domains),"UNS"));
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

  private Thread parseRegistryThread(String contractName, String address, List<String> toPopulateList) {
    return new Thread(() -> {
      String registryAddress = NetworkConfigLoader.getContractAddress(chainId, contractName);
      String deploymentBlock = NetworkConfigLoader.getDeploymentBlock(chainId, contractName);
      Registry registryContract = new Registry(blockchainProviderUrl, registryAddress, provider);
       try {
        List<String> tokensFromRegistry = registryContract.getTokensOwnedBy(address, deploymentBlock);
        toPopulateList.addAll(tokensFromRegistry);
      } catch (NamingServiceException e) {
        e.printStackTrace();
      } 
    });
  }

  @Override
  public List<String> getTokensOwnedBy(String address) throws NamingServiceException {
    List<String> domains = new ArrayList<>();
    List<Thread> threads = Arrays.asList(
      parseRegistryThread("UNSRegistry", address, domains),
      parseRegistryThread("CNSRegistry", address, domains)
    );
    try {
      threads.forEach(Thread::start);
      threads.get(0).join();
      threads.get(1).join();
    } catch(InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new NamingServiceException(NSExceptionCode.UnknownError, NSExceptionParams.EMPTY_PARAMS, e);
    }
    
    return domains;
  }

  @Override
  public String getTokenUri(BigInteger tokenID) throws NamingServiceException {
    try {
      String tokenURI = proxyReaderContract.getTokenUri(tokenID);
      if (tokenURI == null) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getTokenUri", "UNS"));
      }
      return tokenURI;
    } catch (Exception e) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getTokenUri", "UNS"), e);
    }
  }

  @Override
  public String getDomainName(BigInteger tokenID) throws NamingServiceException {
    try {
      String registryAddress = this.getRegistryAddress(tokenID);
      Registry registryContract = new Registry(blockchainProviderUrl, registryAddress, provider);
      String domainName = registryContract.getDomainName(tokenID);
      if (domainName == null) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getDomainName", "UNS"));
      }
      return domainName;
    } catch (Exception e) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getDomainName", "UNS"), e);
    }
  }

  private String getRegistryAddress(BigInteger tokenID) throws NamingServiceException {
    try {
      String tokenURI = proxyReaderContract.registryOf(tokenID);
      if (tokenURI == null) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getRegistryAddress", "UNS"));
      }
      return tokenURI;
    } catch (Exception e) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("m|n", "getRegistryAddress", "UNS"), e);
    }
  }

  protected  ProxyData resolveKey(String key, String domain) throws NamingServiceException {
    return resolveKeys(new String[]{key}, domain);
  }

  private  String getIpfsHash(String domain) throws NamingServiceException {
    String[] keys = {"dweb.ipfs.hash", "ipfs.html.value"};
    ProxyData data = resolveKeys(keys, domain);

    List<String> values = data.getValues();
    if (values.get(0).isEmpty() && values.get(1).isEmpty()) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
              new NSExceptionParams("d|r", domain, keys[0]));
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

  private void checkDomainOwnership(ProxyData data, String domain) throws NamingServiceException {
    if (data.getResolver().isEmpty()) {
      if (data.getOwner().isEmpty()) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, 
          new NSExceptionParams("d", domain));
      }
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver,
      new NSExceptionParams("d", domain));
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

  private ProxyData resolveKeys(String[] keys, String domain) throws NamingServiceException {
    BigInteger tokenID = getTokenID(domain);
    ProxyData data =  proxyReaderContract.getProxyData(keys, tokenID);
    checkDomainOwnership(data, domain);
    return data;
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
