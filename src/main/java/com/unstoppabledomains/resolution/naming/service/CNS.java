
package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.exceptions.ContractCallException;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.Namehash;
import com.unstoppabledomains.resolution.contracts.cns.ProxyData;
import com.unstoppabledomains.resolution.contracts.cns.ProxyReader;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.dns.DnsUtils;
import com.unstoppabledomains.util.Utilities;

import org.apache.commons.lang3.ObjectUtils.Null;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNS extends BaseNamingService {

  private final ProxyReader proxyReaderContract;

  public CNS(NSConfig config) {
    super(config);
    String proxyReaderAddress = NetworkConfigLoader.getContractAddress(config.getChainId(), "ProxyReader");
    this.proxyReaderContract = new ProxyReader(config.getBlockchainProviderUrl(), proxyReaderAddress);
  }

  public Boolean isSupported(String domain) {
    String[] split = domain.split("\\.");
    return (split.length != 0 && split[split.length - 1].equals("crypto"));
  }

  public String getAddress(String domain, String ticker) throws NamingServiceException {
    String key = "crypto." + ticker.toUpperCase() + ".address";
    ProxyData data = resolveKey(key, domain);
    String owner = data.getOwner();
    if (Utilities.isEmptyResponse(owner))
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain,
          new NSExceptionParams("d|c|n", domain, ticker, "CNS"));

    String address = data.getValues().get(0);
    if (Utilities.isEmptyResponse(address))
      throw new NamingServiceException(NSExceptionCode.UnknownCurrency,
          new NSExceptionParams("d|c|n", domain, ticker, "CNS"));
    return address;
  }

  public  String getIpfsHash(String domain) throws NamingServiceException {
    String[] keys = {"dweb.ipfs.hash", "ipfs.html.value"};
    ProxyData data = resolveKeys(keys, domain);

    List<String> values = data.getValues();
    if (values.get(0).isEmpty() && values.get(1).isEmpty()) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
              new NSExceptionParams("d|r", domain, keys[0]));
    }
    return values.get(0).isEmpty() ? values.get(1) : values.get(0);
  }

  public  String getEmail(String domain) throws NamingServiceException {
    String[] keys = {"whois.email.value"};
    ProxyData data = resolveKeys(keys, domain);
    List<String> values = data.getValues();
    if ( values.size() == 0 || Utilities.isEmptyResponse(values.get(0)))
      throw new NamingServiceException(NSExceptionCode.RecordNotFound,
          new NSExceptionParams("d|r", domain, keys[0]));
    return values.get(0);
  }

  public  String getOwner(String domain) throws NamingServiceException {
    try {
      BigInteger tokenID = tokenID(domain);
      String owner = owner(tokenID);
      if (Utilities.isEmptyResponse(owner)) {
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, 
          new NSExceptionParams("d|n", domain, "CNS"));
      }
      return owner;
    } catch (Exception e) {
      throw configureNamingServiceException(e,
          new NSExceptionParams("d|n", domain, "CNS"));
    }
  }

  @Override
  public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException {
    DnsUtils util = new DnsUtils();
    String[] keys = constructDnsRecords(types);
    ProxyData data = resolveKeys(keys, domain);
    List<String> values = data.getValues();
    Map<String, String> rawData = new HashMap<String, String>();
    for (int i = 0; i < values.size(); i++) {
      rawData.put(keys[i], values.get(i));
    }
    return util.toList(rawData);
  }

  protected  ProxyData resolveKey(String key, String domain) throws NamingServiceException {
    return resolveKeys(new String[]{key}, domain);
  }

  private String[] constructDnsRecords(List<DnsRecordsType> types) {
    List<String> records = new ArrayList<String>();
    records.add("dns.ttl");
    for (DnsRecordsType type: types) {
      records.add("dns." + type.toString());
      records.add("dns." + type.toString() + ".ttl");
    }
    return records.toArray(new String[records.size()]);
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
    BigInteger tokenID = tokenID(domain);
    ProxyData data =  proxyReaderContract.getProxyData(keys, tokenID);
    checkDomainOwnership(data, domain);
    return data;
  }


  private String owner(BigInteger tokenID) throws NamingServiceException {
    return proxyReaderContract.getOwner(tokenID);
  }

  private BigInteger tokenID(String domain) throws NamingServiceException {
    String hash = getNamehash(domain);
    return new BigInteger(hash.substring(2), 16);
  }

  @Override
  public String getNamehash(String domain) throws NamingServiceException {
    return Namehash.nameHash(domain);
  }
}
