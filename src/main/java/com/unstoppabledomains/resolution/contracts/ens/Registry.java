package com.unstoppabledomains.resolution.contracts.ens;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Registry extends Contract {

  private static final String ABI_FILE = "ens/ens_registry_abi.json";  
  private static final String namingServiceName = "ENS";

  public Registry(String url, String address) {
    super(namingServiceName, url, address);
  }

  public String getResolverAddress(byte[] tokenId) throws NamingServiceException  {
    Object[] args = new Object[1];
    args[0] = tokenId;
    return fetchAddress("resolver", args);
  }

  public String getOwner(byte[] tokenId) throws NamingServiceException {
    Object[] args = new Object[1];
    args[0] = tokenId;
    return fetchAddress("owner", args);
  }

  @Override
  protected String getAbiPath() {
    return ABI_FILE;
  }
}
