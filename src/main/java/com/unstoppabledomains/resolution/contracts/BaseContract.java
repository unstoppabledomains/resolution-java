package com.unstoppabledomains.resolution.contracts;

import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.cns.ProxyData;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseContract {

  private String namingServiceName;
  private String address;
  private String url;
  private JsonArray abi;
  private IProvider provider;

  protected BaseContract(String namingServiceName, String url, String address, IProvider provider) {
    this.namingServiceName = namingServiceName;
    this.address = address;
    this.url = url;
    this.abi = getAbi();
    this.provider = provider;
  }
  
  protected abstract String getAbiPath();

  protected JsonArray getAbi() {
      String path = getAbiPath();
      final InputStreamReader reader = new InputStreamReader(BaseContract.class.getResourceAsStream(path));

      String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

      return JsonParser.parseString(jsonString).getAsJsonArray();
  }

  protected <T> T fetchOne(String method, Object[] args) throws NamingServiceException {
    Tuple answ = fetchMethod(method, args);
    try {
      return (T) answ.get(0);
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  protected ProxyData fetchData(Object[] args) throws NamingServiceException {
    Tuple answ = fetchMethod("getData", args);
    String resolver = "";
    String owner = "";
    BigInteger resolverValue = (BigInteger) answ.get(0);
    BigInteger ownerValue = (BigInteger) answ.get(1);

    if (resolverValue.intValue() != 0) {  
      resolver = "0x" + ((BigInteger) answ.get(0)).toString(16);
    }
    if (ownerValue.intValue() != 0 ) {
      owner = "0x" + ((BigInteger) answ.get(1)).toString(16);
    }
    List<String> values = Arrays.asList((String[]) answ.get(2));
    return new ProxyData(resolver, owner, values);
  }

  private Tuple fetchMethod(String method, Object[] args) throws NamingServiceException {
    JsonObject methodDescription = getMethodDescription(method, args.length);
    Function function = Function.fromJson(methodDescription.toString());
    ByteBuffer encoded = function.encodeCallWithArgs(args);
    String data = toHexString(encoded.array());
    JsonArray params = prepareParamsForBody(data, address);
    JsonObject body = HTTPUtil.prepareBody("eth_call", params);
    try {
      JsonObject response = provider.request(url, body);
      if (isUnknownError(response)) {
        return new Tuple();
      }
      String answer = response.get("result").getAsString();
      final String replacedAnswer = answer.replace("0x", "");
      return function.decodeReturn(FastHex.decode(replacedAnswer));
    } catch(IOException exception) {
      throw new NamingServiceException(
        NSExceptionCode.BlockchainIsDown,
        new NSExceptionParams("n", namingServiceName),
        exception
      );
    }
  }

  protected String fetchAddress(String method, Object[] args) throws NamingServiceException {
    BigInteger address = fetchOne(method, args);
    if (address == null) {
      return null;
    }
    return "0x" + address.toString(16);
  }

  private String toHexString(byte[] input) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("0x");
    for (byte b : input) {
      stringBuilder.append(String.format("%02x", b & 0xFF));
    }
    return stringBuilder.toString();
  }

  private JsonObject getMethodDescription(String method, int argLen) {
    JsonObject methodDescription;
    for (int i = 0; i < abi.size(); i++) {
      JsonObject m = (JsonObject) abi.get(i);
      JsonArray inputs = (JsonArray) m.get("inputs");
      JsonElement jname = m.get("name");
      if (jname == null) {
        continue;
      }
      String name = jname.getAsString();
      if (name.equals(method) && inputs.size() == argLen) {
        methodDescription = m;
        return methodDescription;
      }
    }
    throw new RuntimeException("Couldn't found method " + method + " from ABI");
  }

  private JsonArray prepareParamsForBody(String data, String address) {
    JsonObject jo = new JsonObject();
    jo.addProperty("data", data);
    jo.addProperty("to", address);
    JsonArray params = new JsonArray();
    params.add(jo);
    params.add("latest");
    return params;
  }

  private boolean isUnknownError(JsonObject response) {
    JsonElement result = response.get("result");
    if (result == null) {
      return response.get("error") != null;
    }
    String answer = result.getAsString();
    return "0x".equals(answer);
  }
}
