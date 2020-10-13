
package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.ContractCallException;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.cns.ProxyReader;

import java.math.BigInteger;
import java.net.UnknownHostException;

public class CNS extends NamingService {

  private static final String PROXY_READER_ADDRESS = "0x7ea9Ee21077F84339eDa9C80048ec6db678642B1";

  private final ProxyReader proxyReaderContract;

  public CNS(String blockchainProviderUrl) {
    this.proxyReaderContract = new ProxyReader(blockchainProviderUrl, PROXY_READER_ADDRESS);
  }

  protected  Boolean isSupported(String domain) {
    String[] split = domain.split("\\.");
    return (split.length != 0 && split[split.length - 1].equals("crypto"));
  }

  protected  String addr(String domain, String ticker) throws NamingServiceException {
    String owner = this.owner(domain);
    if (Utilities.isNull(owner))
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain,
          new NSExceptionParams("d|c|n", domain, ticker, "CNS"));
    String key = "crypto." + ticker.toUpperCase() + ".address";
    String address = this.resolveKey(key, domain);
    if (Utilities.isNull(address))
      throw new NamingServiceException(NSExceptionCode.UnknownCurrency,
          new NSExceptionParams("d|c|n", domain, ticker, "CNS"));
    return address;
  }

  protected  String ipfsHash(String domain) throws NamingServiceException {
    String key = "ipfs.html.value";
    String hash = this.resolveKey(key, domain);

    if (hash == null) {
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver,
              new NSExceptionParams("d|r", domain, key));
    }
    if ("".equals(hash)) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
              new NSExceptionParams("d|r", domain, key));
    }
    return hash;
  }

  protected  String email(String domain) throws NamingServiceException {
    String key = "whois.email.value";
    String email = this.resolveKey(key, domain);
    if (Utilities.isNull(email))
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
          new NSExceptionParams("d|r", domain, key));
    return email;
  }

  protected  String owner(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = this.tokenID(domain);
      String owner = this.owner(tokenID);
      if (Utilities.isNull(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d|n", domain, "CNS"));
      }
      return owner;
    } catch (Exception e) {
      throw this.configureNamingServiceException(e,
          new NSExceptionParams("d|n", domain, "CNS"));
    }
  }

  protected  String resolveKey(String key, String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = this.tokenID(domain);
      return resolveKey(key, tokenID);
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("d|n", domain, "CNS"));
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

  private String resolveKey(String key, BigInteger tokenID) throws Exception {
    return proxyReaderContract.getRecord(key, tokenID);
  }

  private String owner(BigInteger tokenID) throws NamingServiceException {
    String owner = proxyReaderContract.getOwner(tokenID);
    if (Utilities.isNull(owner)) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain);
    }
    return owner;
  }

  private BigInteger tokenID(String domain) throws NamingServiceException {
    String hash = this.namehash(domain);
    return new BigInteger(hash.substring(2), 16);
  }
}
