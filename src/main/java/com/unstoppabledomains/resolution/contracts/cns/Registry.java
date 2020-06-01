package com.unstoppabledomains.resolution.contracts.cns;

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
    String ans = this.fetchOne("resolverOf", args, true);
    if (ans == null) 
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver);
    return ans;
  }

  public String getOwner(BigInteger tokenID) throws Exception {
    try {
      Object[] args = new Object[1];
      args[0] = tokenID;
      return this.fetchOne("ownerOf", args, true);
    } catch (IllegalArgumentException e) {
      // params will be added on level above;
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("", ""), e);
    } catch (Exception e) {
      return null;
    }
  }
}