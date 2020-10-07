package com.unstoppabledomains.resolution;

import java.util.Arrays;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.Contract;
import com.unstoppabledomains.resolution.contracts.ens.EnsContractType;
import com.unstoppabledomains.resolution.contracts.ens.Registry;
import com.unstoppabledomains.resolution.contracts.ens.Resolver;

public class ENS extends NamingService {

  static final String REGISTRY_ADDRESS = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";
  private final String providerURL;
  private final Registry registryContract;
  

  public ENS(String blockchainProviderURL) {
    super();
    this.providerURL = blockchainProviderURL;
    this.registryContract = (Registry)buildContract(REGISTRY_ADDRESS, EnsContractType.Registry);
  }

  @Override
  Boolean isSupported(String domain) {
    String[] ensTLDs = { "eth", "kred", "luxe", "xyz" };
    String[] split = domain.split("\\.");
    String tld = split[split.length - 1];
    return (split.length != 0 && Arrays.asList(ensTLDs).contains(tld));
  }

  @Override
  String addr(String domain, String ticker) throws NamingServiceException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  String ipfsHash(String domain) throws NamingServiceException {
    String resolverAddress = getResolverAddress(domain);
    Resolver resolverContract = (Resolver)buildContract(resolverAddress, EnsContractType.Resolver); 
    return resolverContract.getRecord(domain, "gundb.username.value");
  }

  @Override
  String email(String domain) throws NamingServiceException {
    Resolver resolver = getResolverContract(domain);
    return resolver.getRecord(domain, "whois.email.value");
  }

  @Override
  String owner(String domain) throws NamingServiceException {
   byte[] tokenId = this.tokenID(domain);
   return this.registryContract.getOwner(tokenId);
  }

  private Resolver getResolverContract(String domain) throws NamingServiceException {
    String resolverAddress = getResolverAddress(domain);
    return (Resolver) buildContract(resolverAddress, EnsContractType.Resolver);
  }


  private byte[] tokenID(String domain) {
    String hash = this.namehash(domain);
    return Numeric.hexStringToByteArray(hash);
  }

  private String getResolverAddress(String domain) throws NamingServiceException {
    byte[] tokenId = this.tokenID(domain);
    return this.registryContract.getResolverAddress(tokenId);
  }

  private Contract buildContract(String address, EnsContractType type) {
    if (type.equals(EnsContractType.Resolver)) {
      return new Resolver(this.providerURL, address);
    }
    return new Registry(this.providerURL, address);
  }
}
