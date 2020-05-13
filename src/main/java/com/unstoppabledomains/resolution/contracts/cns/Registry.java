package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;
import java.math.BigInteger;

public class Registry extends Contract {

  public Registry(String url, String address) throws Exception {
    super(url, address, "registry", "abi/cns");
  }

  public void getResolver(String address) {
    Object[] args = new Object[1];
    args[0] = new BigInteger(address.replace("0x", ""), 16);
    this.fetchMethod("resolverOf", args);
  }
}