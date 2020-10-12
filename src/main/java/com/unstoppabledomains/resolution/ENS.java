package com.unstoppabledomains.resolution;

import java.util.Arrays;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.Contract;
import com.unstoppabledomains.resolution.contracts.ens.EnsContractType;
import com.unstoppabledomains.resolution.contracts.ens.Registry;
import com.unstoppabledomains.resolution.contracts.ens.Resolver;

public class ENS extends NamingService {

  private static final String REGISTRY_ADDRESS = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";
  private final String providerURL;
  private final Registry registryContract;
  

  public ENS(String blockchainProviderURL) {
    super();
    this.providerURL = blockchainProviderURL;
    this.registryContract = (Registry)buildContract(REGISTRY_ADDRESS, EnsContractType.Registry);
  }

  @Override
  protected Boolean isSupported(String domain) {
    String[] ensTLDs = { "eth", "kred", "luxe", "xyz" };
    String[] split = domain.split("\\.");
    String tld = split[split.length - 1];
    return (split.length != 0 && Arrays.asList(ensTLDs).contains(tld));
  }

  @Override
  protected String addr(String domain, String ticker) throws NamingServiceException {
    if (!ticker.equalsIgnoreCase("ETH")) {
      throw new NamingServiceException(NSExceptionCode.UnsupportedCurrency, new NSExceptionParams("c", ticker.toUpperCase()));
    }
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = this.tokenId(domain);
    return resolver.addr(tokenId, ticker.toUpperCase());
  }

  @Override
  protected String email(String domain) throws NamingServiceException {
    Resolver resolver = getResolverContract(domain);
    byte[] tokenId = this.tokenId(domain);
    String emailRecord = resolver.getTextRecord(tokenId, "whois.email.value");
    if (Utilities.isNull(emailRecord)) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d|r", domain, "email"));
    }
    return emailRecord;
  }

  @Override
  protected String owner(String domain) throws NamingServiceException {
   byte[] tokenId = this.tokenId(domain);
   String owner = this.registryContract.getOwner(tokenId);
    if (Utilities.isNull(owner)) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
    }
    return owner;
  }

  private Resolver getResolverContract(String domain) throws NamingServiceException {
    String resolverAddress = getResolverAddress(domain);
    if (Boolean.TRUE.equals(Utilities.isNull(resolverAddress))) {
      String owner = this.registryContract.getOwner(this.tokenId(domain));
      if (Utilities.isNull(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
      }
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver, new NSExceptionParams("d", domain));
    }
    return (Resolver) buildContract(resolverAddress, EnsContractType.Resolver);
  }
  
  private byte[] tokenId(String domain) {
    String hash = this.namehash(domain);
    return Numeric.hexStringToByteArray(hash);
  }

  private String getResolverAddress(String domain) {
    byte[] tokenId = this.tokenId(domain);
    return this.registryContract.getResolverAddress(tokenId);
  }

  private Contract buildContract(String address, EnsContractType type) {
    if (type.equals(EnsContractType.Resolver)) {
      return new Resolver(this.providerURL, address);
    }
    return new Registry(this.providerURL, address);
  }

  @Override
  protected String ipfsHash(String domain) throws NamingServiceException {
    throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m", "ENS"));
  }
}
