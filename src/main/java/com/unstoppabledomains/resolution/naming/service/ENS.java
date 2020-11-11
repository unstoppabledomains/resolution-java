package com.unstoppabledomains.resolution.naming.service;

import java.util.Arrays;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.Namehash;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.ens.EnsContractType;
import com.unstoppabledomains.resolution.contracts.ens.Registry;
import com.unstoppabledomains.resolution.contracts.ens.Resolver;
import com.unstoppabledomains.util.Utilities;

public class ENS extends BaseNamingService {

  private static final String REGISTRY_ADDRESS = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";

  private final Registry registryContract;

  public ENS(NSConfig config) {
    super(config);
    this.registryContract = (Registry) buildContract(REGISTRY_ADDRESS, EnsContractType.Registry);
  }

  @Override
  public Boolean isSupported(String domain) {
    String[] ensTLDs = { "eth", "kred", "luxe", "xyz" };
    String[] split = domain.split("\\.");
    String tld = split[split.length - 1];
    return (split.length != 0 && Arrays.asList(ensTLDs).contains(tld));
  }

  @Override
  public String getAddress(String domain, String ticker) throws NamingServiceException {
    if (!ticker.equalsIgnoreCase("ETH")) {
      throw new NamingServiceException(NSExceptionCode.UnsupportedCurrency, new NSExceptionParams("c", ticker.toUpperCase()));
    }
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = tokenId(domain);
    return resolver.addr(tokenId, ticker.toUpperCase());
  }

  @Override
  public String getEmail(String domain) throws NamingServiceException {
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = tokenId(domain);
    String emailRecord = resolver.getTextRecord(tokenId, "whois.email.value");
    if (Utilities.isEmptyResponse(emailRecord)) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d|r", domain, "getEmail"));
    }
    return emailRecord;
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

  @Override
  public String getIpfsHash(String domain) throws NamingServiceException {
    throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m|n", "getIpfsHash" ,"ENS"));
  }

  @Override
  public String getNamehash(String domain) throws NamingServiceException {
    return Namehash.nameHash(domain);
  }
}
