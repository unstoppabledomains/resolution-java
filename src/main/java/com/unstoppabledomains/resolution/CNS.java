
package com.unstoppabledomains.resolution;

import java.math.BigInteger;
import java.net.UnknownHostException;

import org.web3j.protocol.Web3j;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.DefaultGasProvider;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.generated.Cryptoregistry;
import com.unstoppabledomains.resolution.contracts.generated.Cryptoresolver;

public class CNS extends NamingService {
  final static String registryAddress = "0xD1E5b0FF1287aA9f9A268759062E4Ab08b9Dacbe";
  Cryptoregistry registryContract;

  public CNS(Web3j web3, Boolean verbose) {
    super("CNS", web3, verbose);
    this.registryContract = this.buildRegistry();
  }

  public Boolean isSupported(String domain) {
    String[] split = domain.split("\\.");
    return (split.length != 0 && split[split.length - 1].equals("crypto"));
  }

  public String addr(String domain, String ticker) throws NamingServiceException {
    String owner = this.owner(domain);
    if (this.isNull(owner))
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain,
          new NSExceptionParams("d|c|n", domain, ticker, "CNS"));
    String key = "crypto." + ticker.toUpperCase() + ".address";
    String address = this.resolveKey(key, domain);
    if (this.isNull(address))
      throw new NamingServiceException(NSExceptionCode.UnknownCurrency, new NSExceptionParams("d|c|n", domain, ticker, "CNS"));
    return address;
  }

  public String ipfsHash(String domain) throws NamingServiceException {
    String key = "ipfs.html.value";
    String hash = this.resolveKey(key, domain);
    if (isNull(hash))
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d", domain));
    return hash;

  }

  public String email(String domain) throws NamingServiceException {
    String key = "whois.email.value";
    String email = this.resolveKey(key, domain);
    if (isNull(email))
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d", domain));
    return email;
  }

  public String owner(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = this.tokenID(domain);
      String owner = this.owner(tokenID);
      return owner;
    } catch (Exception e) {
      throw this.configureNamingServiceException(e, new NSExceptionParams("d|n", domain, "CNS"));
    }
  }

  public String resolverAddress(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = this.tokenID(domain);
      return this.resolverAddress(tokenID);
    } catch (Exception e) {
      throw this.configureNamingServiceException(e, new NSExceptionParams("d|n", domain, "CNS"));
    }
  }

  public String resolveKey(String key, String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = this.tokenID(domain);
      return resolveKey(key, tokenID);
    } catch (Exception e) {
      throw configureNamingServiceException(e, new NSExceptionParams("d|n", domain, "CNS"));
    }
  }

  private NamingServiceException configureNamingServiceException(Exception e, NSExceptionParams params) {
    e.printStackTrace();
    if (e instanceof UnknownHostException) {
      return new NamingServiceException(NSExceptionCode.BlockchainIsDown, params, e);
    } else if (e instanceof ContractCallException) {
      return new NamingServiceException(NSExceptionCode.RecordNotFound, params, e);
    }
    return new NamingServiceException(NSExceptionCode.UnknownError, params, e);
  }

  private String resolveKey(String key, BigInteger tokenID) throws Exception {
    Cryptoresolver resolverContract = this.loadResolver(this.resolverAddress(tokenID));
    return resolverContract.get(key, tokenID).send();
  }

  private String resolverAddress(BigInteger tokenID) throws Exception {
    return this.registryContract.resolverOf(tokenID).send();
  }

  private String owner(BigInteger tokenID) throws Exception {
    return this.registryContract.ownerOf(tokenID).send();
  }

  private BigInteger tokenID(String domain) {
    String hash = this.namehash(domain);
    return new BigInteger(hash.substring(2), 16);
  }

  private Cryptoregistry buildRegistry() {
    return Cryptoregistry.load(CNS.registryAddress, CNS.web3, CNS.transactionManager, new DefaultGasProvider());
  }

  private Cryptoresolver loadResolver(String address) {
    return Cryptoresolver.load(address, CNS.web3, CNS.transactionManager, new DefaultGasProvider());
  }
}