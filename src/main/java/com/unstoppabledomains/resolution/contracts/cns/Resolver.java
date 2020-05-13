package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Resolver extends Contract {

  public Resolver(String url, String address) throws Exception {
    super(url, address, "resolver", "abi/cns");
  }
}