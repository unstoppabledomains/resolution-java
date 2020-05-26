package com.unstoppabledomains.resolution.contracts.cns;

import com.esaulpaugh.headlong.abi.Tuple;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;
import java.math.BigInteger;

public class Registry extends Contract {

  public Registry(String url, String address) {
    super(url, address, "registry", "abi/cns");
  }

  public String getResolver(BigInteger tokenID) throws Exception {
    Object[] args = new Object[1];
    args[0] = tokenID;
    Tuple answ = this.fetchMethod("resolverOf", args);
    if (answ.size() == 0) throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver);
    BigInteger resolverAddress = new BigInteger(answ.get(0).toString());
    return "0x" + resolverAddress.toString(16);
  }

  public String getOwner(BigInteger tokenID) throws Exception {
    Object[] args = new Object[1];
    args[0] = tokenID; 
    try {
      Tuple answ = this.fetchMethod("ownerOf", args);
      BigInteger ownerAddress = new BigInteger(answ.get(0).toString());
      return "0x" + ownerAddress.toString(16);
    } catch(Exception e) {
      if (e instanceof IllegalArgumentException) {
        // params will be added on level above.
        throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("", ""), e);
      }
      return null;
    }
  }
}