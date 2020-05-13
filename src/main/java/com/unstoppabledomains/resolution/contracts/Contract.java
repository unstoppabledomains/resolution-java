package com.unstoppabledomains.resolution.contracts;

import java.io.FileReader;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Arrays;

import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.ethereum.crypto.cryptohash.Keccak256;

public class Contract extends HTTPUtil {
  protected String address;
  protected String type;
  protected String url;
  protected JsonArray abi;

  public Contract(String url, String address, String type, String pathToAbi) throws Exception {
    if (address == null || address.isEmpty())
      throw new Exception("Wrong address input: " + address);
    JsonParser parser = new JsonParser();
    String jsonAbiPath = type.equals("registry") ? pathToAbi + "/registry.json" : pathToAbi + "/`resolver.json";
    System.out.println(jsonAbiPath);
    JsonArray parsed = (JsonArray) parser.parse(new FileReader(jsonAbiPath));
    this.url = url;
    this.abi = parsed;
    this.address = address;
    this.type = type;
  }

  public Object fetchMethod(String method, Object[] args) {
    JsonObject methodDescription = this.getMethodDescription(method, args.length);
    try {
      Function f = Function.fromJson(methodDescription.toString());
      ByteBuffer encoded = f.encodeCallWithArgs(args);
      String data = this.toHexString(encoded.array());
      byte[] response = this.post(this.url, this.address, data);
      // String resultEncoded = response.get("result").getAsString();
      // System.out.println(resultEncoded);
      // System.out.println("bytes = " + resultEncoded.getBytes());
      Tuple answer = f.decodeReturn(response);
      System.out.println( answer.toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // next step is to encode input arguments according to their types and send the call to etherium provider.
    return "";
  }

  private String toHexString(byte[] input) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("0x");
    for (int i = 0; i <  input.length; i++) {
        stringBuilder.append(String.format("%02x", input[i] & 0xFF));
    }
    return stringBuilder.toString();
  }

  private JsonObject getMethodDescription(String method, int argLen) {
    JsonObject methodDescription = null;
    for (int i = 0; i < this.abi.size(); i++) {
      JsonObject m = (JsonObject) this.abi.get(i);
      JsonArray inputs = (JsonArray) m.get("inputs");
      String name = m.get("name").getAsString();
      if (name.equals(method) && inputs.size() == argLen) {
        System.out.println("catched method = " + m.toString());
        methodDescription = m;
        break ;
      }
    }
    return methodDescription;
  }
}
