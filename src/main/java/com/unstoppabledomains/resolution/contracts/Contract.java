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
import org.web3j.utils.Numeric;
import org.ethereum.crypto.cryptohash.Keccak256;

public class Contract {
  protected String address;
  protected String type;
  protected JSONArray abi;

  public Contract(String address, String type, String pathToAbi) throws Exception {
    if (address == null || address.isEmpty())
      throw new Exception("Wrong address input: " + address);
    JSONParser parser = new JSONParser();
    String jsonAbiPath = type.equals("registry") 
      ? pathToAbi + "/registry.json"
      : pathToAbi + "/`resolver.json";
    System.out.println(jsonAbiPath);
    JSONArray parsed = (JSONArray) parser.parse(new FileReader(jsonAbiPath));
    this.abi = parsed;
    this.address = address;
    this.type = type;
  }

  public Object fetchMethod(String method, String[] args) {
    JSONObject methodDescription = this.getMethodDescription(method, args.length);
    String functionSignature = this.getMethodSignature(methodDescription);
    System.out.println(functionSignature);
    System.out.println(this.encodeSignature(functionSignature));
    // next step is to encode input arguments according to their types and send the call to etherium provider.
    return "";
  }


  private String encodeSignature(String signature) {
    Keccak256 digest =  new Keccak256();
    digest.update(signature.getBytes());
    byte[] hash = digest.digest();
    // todo See if we can find a different library to do the conversion
    return Numeric.toHexString(hash).substring(0, 10);
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

  private String[] getMethodInputTypes(JSONArray inputs) {
    String[] inputTypes = new String[inputs.size()];
    for (int i = 0; i < inputs.size(); i++) {
      JSONObject inputDescription = (JSONObject) inputs.get(i);
      inputTypes[i] = (String) inputDescription.get("type");
    }
    return inputTypes;
  }

  private String getMethodSignature(JSONObject methodDescription) {
    String[] inputTypes = this.getMethodInputTypes((JSONArray) methodDescription.get("inputs"));
    String methodName = (String) methodDescription.get("name");
    return this.getMethodSignature(methodName, inputTypes);
  }

  private String getMethodSignature(String methodName, String[] types) {
    String methodSignature = methodName + '(';
    for (int i = 0; i < types.length; i++) {
      if (i == 0) {
        methodSignature = methodSignature + types[i];
        continue;
      }
      methodSignature = methodSignature + ',' + types[i];
    }
    methodSignature = methodSignature + ')';
    return methodSignature;
  }
}
