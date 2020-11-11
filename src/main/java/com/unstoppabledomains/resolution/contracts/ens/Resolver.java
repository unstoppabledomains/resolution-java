package com.unstoppabledomains.resolution.contracts.ens;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.BaseContract;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Resolver extends BaseContract {

  private static final String ABI_FILE = "ens/ens_resolver_abi.json";
  private static final String namingServiceName = "ENS";
  private final Map<String, String> uDRecordsToENS = new HashMap<>();

  public Resolver(String url, String address) {
    super(namingServiceName, url, address);
    configureRecordsMap();
  }

  public String addr(byte[] tokenId, String ticker) throws NamingServiceException {
    Object[] args = new Object[2];
    args[0] = tokenId;
    // TODO update when multi-coin support is finished
    //? eth coinType = 60
    args[1] = new BigInteger("60");
    byte[] addressBytes = fetchOne("addr", args);
    if (addressBytes == null) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("r", ticker));
    }
    return Numeric.toHexString(addressBytes);
  }

public String getTextRecord(byte[] tokenId, String key) throws NamingServiceException {
    String ensRecordKey = uDRecordsToENS.get(key);
    Object[] args = new Object[2];
    args[0] = tokenId;
    args[1] = ensRecordKey;
    
    String record = fetchOne("text", args);
    if (record == null) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("r", key));
    }
    return record;
  }

  private void configureRecordsMap() {
    uDRecordsToENS.put("ipfs.redirect_domain.value", "url");
    uDRecordsToENS.put("whois.email.value", "email");
    uDRecordsToENS.put("gundb.username.value", "gundb_username");
    uDRecordsToENS.put("gundb.public_key.value", "gundb_public_key");
  }

  @Override
  protected String getAbiPath() {
    return ABI_FILE;
  }
}
