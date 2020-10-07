package com.unstoppabledomains.resolution.contracts.ens;

import java.io.IOException;
import java.math.BigInteger;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Registry extends Contract {

  private static final String ABI_FILE = "src/main/resources/abi/ens_registry_abi.json";  

  public Registry(String url, String address) {
    super(url, address, ABI_FILE);
  }

  public String getResolverAddress(byte[] tokenId) throws NamingServiceException {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("resolver", args);
    } catch(IOException exception) {
      throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver);
    }
  }

  public String getOwner(byte[] tokenId) throws NamingServiceException {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("owner", args);
    } catch(IOException exception) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain);
    }
  }

}
