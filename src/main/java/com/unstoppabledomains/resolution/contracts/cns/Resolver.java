package com.unstoppabledomains.resolution.contracts.cns;

import java.math.BigInteger;

import com.esaulpaugh.headlong.abi.Tuple;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Resolver extends Contract {

  public Resolver(String url, String address) throws Exception {
    super(url, address, "resolver", "abi/cns");
  }

  public String getRecord(String recordKey, BigInteger tokenID) throws Exception {
    Object[] args = new Object[2];
    args[0] = recordKey;
    args[1] = tokenID;
    Tuple answ = this.fetchMethod("get", args);
    return answ.get(0).toString();
  }

  public String getResolver(BigInteger tokenID) throws Exception {
    Object[] args = new Object[1];
    args[0] = tokenID;
    Tuple answ = this.fetchMethod("resolverOf", args);
    if (answ.size() == 0) throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver);
    BigInteger resolverAddress = new BigInteger(answ.get(0).toString());
    return resolverAddress.toString(16);
  }



}
