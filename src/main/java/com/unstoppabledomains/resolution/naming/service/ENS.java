package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.Namehash;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.ens.EnsContractType;
import com.unstoppabledomains.resolution.contracts.ens.Registry;
import com.unstoppabledomains.resolution.contracts.ens.Resolver;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.util.Utilities;

import java.util.Arrays;
import java.util.List;

public class ENS extends BaseNamingService {
  
  private static final String REGISTRY_ADDRESS = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";

  private final Registry registryContract;

  public ENS(NSConfig config) {
    super(config);
    this.registryContract = (Registry) buildContract(REGISTRY_ADDRESS, EnsContractType.Registry);
  }

  @Override
  public NamingServiceType getName() {
    return NamingServiceType.ENS;
  }

  @Override
  public Boolean isSupported(String domain) {
    String[] ensTLDs = { "eth", "kred", "luxe", "xyz" };
    String[] split = domain.split("\\.");
    String tld = split[split.length - 1];
    return (split.length != 0 && Arrays.asList(ensTLDs).contains(tld));
  }

  @Override
  public String getRecord(String domain, String recordKey) throws NamingServiceException {
    if (recordKey.startsWith("crypto.")) {
      String ticker = recordKey.split("\\.")[1];
      return getAddress(domain, ticker);
    }
    if (recordKey.equals("ipfs.html.value") || recordKey.equals("dweb.ipfs.hash")) {
      return getIpfsHash(domain);
    }
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = tokenId(domain);
    String record = resolver.getTextRecord(tokenId, recordKey);
    if (Utilities.isEmptyResponse(record)) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
        new NSExceptionParams("d|r", domain, recordKey));
    }
    return record;
  }

  @Override
  public String getOwner(String domain) throws NamingServiceException {
   byte[] tokenId = tokenId(domain);
   String owner = registryContract.getOwner(tokenId);
    if (Utilities.isEmptyResponse(owner)) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
    }
    return owner;
  }

  @Override
  public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException {
    throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m|n", "getDns", "ENS"));
  }

  private String getAddress(String domain, String ticker) throws NamingServiceException {
    if (!ticker.equalsIgnoreCase("ETH")) {
      throw new NamingServiceException(NSExceptionCode.UnsupportedCurrency, 
        new NSExceptionParams("c", ticker.toUpperCase()));
    }
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = tokenId(domain);
    return resolver.addr(tokenId, ticker.toUpperCase());
  }

  private Resolver getResolverContract(String domain) throws NamingServiceException {
    String resolverAddress = getResolverAddress(domain);
    if (Boolean.TRUE.equals(Utilities.isEmptyResponse(resolverAddress))) {
      String owner = registryContract.getOwner(tokenId(domain));
      if (Utilities.isEmptyResponse(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
      }
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver, new NSExceptionParams("d", domain));
    }
    return (Resolver) buildContract(resolverAddress, EnsContractType.Resolver);
  }
  
  private byte[] tokenId(String domain) throws NamingServiceException {
    String hash = getNamehash(domain);
    return Numeric.hexStringToByteArray(hash);
  }

  private String getResolverAddress(String domain) throws NamingServiceException {
    byte[] tokenId = tokenId(domain);
    return registryContract.getResolverAddress(tokenId);
  }

  private BaseContract buildContract(String address, EnsContractType type) {
    if (type.equals(EnsContractType.Resolver)) {
      return new Resolver(blockchainProviderUrl, address);
    }
    return new Registry(blockchainProviderUrl, address);
  }

  private String getIpfsHash(String domain) throws NamingServiceException {
    throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m|n", "getIpfsHash" ,"ENS"));
  }

  @Override
  public String getNamehash(String domain) throws NamingServiceException {
    return Namehash.nameHash(domain);
  }
}
