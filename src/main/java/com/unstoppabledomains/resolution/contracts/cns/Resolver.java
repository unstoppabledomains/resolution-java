package com.unstoppabledomains.resolution.contracts.cns;

import java.math.BigInteger;

import com.unstoppabledomains.resolution.contracts.Contract;

public class Resolver extends Contract {

  public Resolver(String url, String address) throws Exception {
    super(url, address, "resolver", "abi/cns");
  }

  public String getRecord(String recordKey, BigInteger tokenID) throws Exception {
    Object[] args = new Object[2];
    args[0] = recordKey;
    args[1] = tokenID;
    return this.fetchOne("get", args);
  }
}
