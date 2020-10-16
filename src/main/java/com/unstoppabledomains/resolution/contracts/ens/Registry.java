package com.unstoppabledomains.resolution.contracts.ens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

public class Registry extends Contract {

  private static final String ABI_FILE = "ens_registry_abi.json";  


  public Registry(String url, String address) {
    super(url, address);
  }

  public String getResolverAddress(byte[] tokenId) throws NamingServiceException  {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("resolver", args);
    } catch(IOException exception) {
      throw new NamingServiceException(NSExceptionCode.BlockchainIsDown, new NSExceptionParams("n", "ENS"), exception);
    }
  }

  public String getOwner(byte[] tokenId) throws NamingServiceException {
    Object[] args = new Object[1];
    args[0] = tokenId;
    try {
      return this.fetchAddress("owner", args);
    } catch(IOException exception) {
      throw new NamingServiceException(NSExceptionCode.BlockchainIsDown, new NSExceptionParams("n", "ENS"), exception);
    }
  }

  @Override
  protected JsonArray getAbi() {
      final InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(ABI_FILE));

      String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

      return new JsonParser().parse(jsonString).getAsJsonArray();
  }
}
