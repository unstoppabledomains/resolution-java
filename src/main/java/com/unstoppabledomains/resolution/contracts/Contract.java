package com.unstoppabledomains.resolution.contracts;

import java.io.FileReader;
import java.io.IOException;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Contract {
  protected String address;
  protected String type;
  protected JSONArray abi;

  public Contract(String address, String type, String pathToAbi) throws NamingServiceException {
    JSONParser parser = new JSONParser();
    try {
      String jsonAbiPath = type.equals("registry") 
        ? pathToAbi + "/registry.json"
        : pathToAbi + "/`resolver.json";
      System.out.println(jsonAbiPath);
      JSONArray parsed = (JSONArray) parser.parse(new FileReader(jsonAbiPath));
      this.abi = parsed;
    } catch (IOException | ParseException e) {
      throw new NamingServiceException(NSExceptionCode.UnknownError, null, e);
    }
    if (address == null || address.isEmpty())
      throw new NamingServiceException(NSExceptionCode.IncorrectContractAddress,
          new NSExceptionParams("a", address));
    this.address = address;
    this.type = type;
  }

  public Object fetchMethod(String method, String[] args) {
    JSONObject methodDescription = this.getMethodDescription(method, args.length);
    System.out.println(methodDescription);
    return "";
  }


  private JSONObject getMethodDescription(String method, int argLen) {
    JSONObject methodDescription = null;
    for (int i = 0; i < this.abi.size(); i++) {
      JSONObject m = (JSONObject) this.abi.get(i);
      JSONArray inputs = (JSONArray) m.get("inputs");
      if (m.get("name").equals(method) && inputs.size() == argLen) {
        methodDescription = m;
        break ;
      }
    }
    return methodDescription;
  }
}