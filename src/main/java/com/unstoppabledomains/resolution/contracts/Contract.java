package com.unstoppabledomains.resolution.contracts;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.ParseException;

import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Contract {
  protected String address;
  protected String url;
  protected JsonArray abi;

  public Contract(String url, String address, JsonArray abi) {
    this.abi = abi;
    this.url = url;
    this.address = address;
  }

  public Tuple fetchMethod(String method, Object[] args) throws ParseException, IOException {
    JsonObject methodDescription = this.getMethodDescription(method, args.length);
    Function f = Function.fromJson(methodDescription.toString());
    ByteBuffer encoded = f.encodeCallWithArgs(args);
    String data = this.toHexString(encoded.array());
    JsonObject response = HTTPUtil.post(this.url, this.address, data);
    String answer = response.get("result").getAsString().replace("0x", "");
    if (answer.equals(""))
      return new Tuple();
    Tuple answ = f.decodeReturn(FastHex.decode(answer));
    return answ;
  }

  protected <T> T fetchOne(String method, Object[] args) throws ParseException, IOException {
    Tuple answ = this.fetchMethod(method, args);
    try {
      return (T) answ.get(0);
    } catch (ArrayIndexOutOfBoundsException e) {
    }
    return null;
  }

  protected String fetchAddress(String method, Object[] args) throws ParseException, IOException {
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
    JsonObject methodDescription = null;
    for (int i = 0; i < this.abi.size(); i++) {
      JsonObject m = (JsonObject) this.abi.get(i);
      JsonArray inputs = (JsonArray) m.get("inputs");
      JsonElement jname = m.get("name");
      if (jname == null)
        continue;
      String name = jname.getAsString();
      if (name.equals(method) && inputs.size() == argLen) {
        methodDescription = m;
        break;
      }
    }
    return methodDescription;
  }
}
