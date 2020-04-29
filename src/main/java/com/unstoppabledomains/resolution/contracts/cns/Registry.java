package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Registry extends Contract {

  public Registry(String address) throws NamingServiceException {
    super(address, "registry", "abi/cns");
  }

  public void getResolver(String address) {
    String[] args = new String[1];
    args[0] = address;
    this.fetchMethod("resolverOf", args);
  }
}