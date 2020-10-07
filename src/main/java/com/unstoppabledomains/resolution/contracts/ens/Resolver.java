package com.unstoppabledomains.resolution.contracts.ens;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.NameHash;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Resolver extends Contract {

  private static final String ABI_FILE = "src/main/resources/abi/ens_resolver_abi.json";  
  private static final Map<String, String> UDRecordsToENS = new HashMap<>();

  public Resolver(String url, String address) {
    super(url, address, ABI_FILE);
    configureRecordsMap();
  }

  public String getRecord(String domain, String key) throws NamingServiceException {
    byte[] tokenId = this.tokenID(domain);
    return getRecord(tokenId, key);
  }

  public String getRecord(byte[] tokenId, String key) throws NamingServiceException {
    String ensRecordKey = UDRecordsToENS.get(key);
    Object[] args = new Object[2];
    args[0] = tokenId;
    args[1] = ensRecordKey;
    try {
      return this.fetchOne("text", args);
    } catch(IOException exception) {
      throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("r", key));
    }
  }

  private byte[] tokenID(String domain) {
    String hash = NameHash.nameHash(domain);
    return Numeric.hexStringToByteArray(hash);
  }

  private void configureRecordsMap() {
    UDRecordsToENS.put("ipfs.redirect_domain.value", "url");
    UDRecordsToENS.put("whois.email.value", "email");
    UDRecordsToENS.put("gundb.username.value", "gundb_username");
    UDRecordsToENS.put("gundb.public_key.value", "gundb_public_key");
  }

}
