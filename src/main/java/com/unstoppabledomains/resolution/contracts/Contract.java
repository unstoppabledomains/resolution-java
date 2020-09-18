package com.unstoppabledomains.resolution.contracts;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.unstoppabledomains.resolution.Utilities;

public class Contract {
  protected String address;
  protected String url;
  protected JsonArray abi;

  public Contract(String url, String address, JsonArray abi) {
    this.abi = abi;
    this.url = url;
    this.address = address;
  }

  public Tuple fetchMethod(String method, Object[] args) throws IOException {
    JsonObject methodDescription = this.getMethodDescription(method, args.length);
    if (methodDescription == null) throw new IOException("Couldn't found method from ABI");
    Function f = Function.fromJson(methodDescription.toString());
    ByteBuffer encoded = f.encodeCallWithArgs(args);
    String data = this.toHexString(encoded.array());
    JsonArray params = prepareParamsForBody(data, this.address);
    JsonObject body = HTTPUtil.prepareBody("eth_call", params);
    JsonObject response = HTTPUtil.post(this.url, body);
    String answer = response.get("result").getAsString().replace("0x", "");
    if (Utilities.isNull(answer))
      return new Tuple();
    return f.decodeReturn(FastHex.decode(answer));
  }

  protected <T> T fetchOne(String method, Object[] args) throws IOException {
    Tuple answ = this.fetchMethod(method, args);
    try {
      return (T) answ.get(0);
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  protected String fetchAddress(String method, Object[] args) throws IOException {
    BigInteger address = this.fetchOne(method, args);
    if (address == null) return null;
    return "0x" + address.toString(16);
  }

  protected String toHexString(byte[] input) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("0x");
    for (int i = 0; i < input.length; i++) {
      stringBuilder.append(String.format("%02x", input[i] & 0xFF));
    }
    return stringBuilder.toString();
  }

  private JsonObject getMethodDescription(String method, int argLen) {
    JsonObject methodDescription;
    for (int i = 0; i < this.abi.size(); i++) {
      JsonObject m = (JsonObject) this.abi.get(i);
      JsonArray inputs = (JsonArray) m.get("inputs");
      JsonElement jname = m.get("name");
      if (jname == null)
        continue;
      String name = jname.getAsString();
      if (name.equals(method) && inputs.size() == argLen) {
        methodDescription = m;
        return methodDescription;
      }
    }
    return null;
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
}
