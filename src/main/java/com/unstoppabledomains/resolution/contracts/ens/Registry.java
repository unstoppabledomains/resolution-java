package com.unstoppabledomains.resolution.contracts.ens;

import java.io.IOException;

import com.unstoppabledomains.resolution.contracts.Contract;

public class Registry extends Contract {

  private static final String ABI_FILE = "src/main/resources/abi/ens_registry_abi.json";  

  public Registry(String url, String address) {
    super(url, address, ABI_FILE);
  }

  public String getResolverAddress(byte[] tokenId) {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("resolver", args);
    } catch(IOException exception) {
      return null;
    }
  }

  public String getOwner(byte[] tokenId) {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("owner", args);
    } catch(IOException exception) {
      return null;
    }
  }

}
