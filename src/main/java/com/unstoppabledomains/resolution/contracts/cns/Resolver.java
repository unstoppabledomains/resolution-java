package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Resolver extends Contract {

  public Resolver(String address) throws Exception {
    super(address, "resolver", "abi/cns");
  }
}