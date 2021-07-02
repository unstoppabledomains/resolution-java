package com.unstoppabledomains.resolution.contracts;

import com.esaulpaugh.headlong.abi.Event;
import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Hash;
import com.unstoppabledomains.resolution.contracts.uns.ProxyData;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public abstract class BaseContract {
  private Gson gson;

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

    gson = new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create();
  }
  
  protected abstract String getAbiPath();

  protected JsonArray getAbi() {
      String path = getAbiPath();
      final JsonReader jsonReader =
                new JsonReader( new InputStreamReader(BaseContract.class.getResourceAsStream(path)));
      return new Gson().fromJson(jsonReader, JsonArray.class);
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

  protected List<Tuple> fetchLogs(String fromBlock, String eventName, String[] topics) throws NamingServiceException {
    Event event = Event.fromJsonObject(getEventDescription(eventName));
    JsonArray params = prepareParamsForLogs(fromBlock, Hash.sha3String(event.signature()), topics);
    JsonObject body = HTTPUtil.prepareBody("eth_getLogs", params);
    try {
      List<Tuple> logs = new ArrayList<>();
      JsonObject response = provider.request(url, body);
      if (isUnknownError(response)) {
        return logs;
      }
      JsonArray answerArray = response.get("result").getAsJsonArray();
      for (JsonElement jsonElement : answerArray) {
        ContractLogs logElement = gson.fromJson(jsonElement.toString(), ContractLogs.class);
        final String hexData = logElement.getData().replace("0x", "");
        Tuple decodedParams = event.getNonIndexedParams().decode(FastHex.decode(hexData));
        logs.add(decodedParams);
      }
      return logs;
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

  private JsonObject getEventDescription(String event) {
    for (JsonElement element : abi) {
      JsonObject elementObject = (JsonObject) element;

      JsonElement jname = elementObject.get("name");
      if (jname == null) {
        continue;
      }

      String name = jname.getAsString();
      if (name.equals(event)) {
        return elementObject;
      }
    }
    throw new RuntimeException("Couldn't find event " + event + " from ABI");
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

  private JsonArray prepareParamsForLogs(String fromBlock, String event, String[] topics) {
    JsonObject jo = new JsonObject();
    jo.addProperty("fromBlock", fromBlock);
    jo.addProperty("toBlock", "latest");
    jo.addProperty("address", this.address);
    JsonArray topicsJson = new JsonArray();
    topicsJson.add(event);
    for (String topic : topics) {
      topicsJson.add(topic);
    }
    jo.add("topics", topicsJson);
    JsonArray params = new JsonArray();
    params.add(jo);
    return params;
  }

  private boolean isUnknownError(JsonObject response) {
    JsonElement result = response.get("result");
    if (result == null) {
      return response.get("error") != null;
    }
    if (!result.isJsonPrimitive()) {
      return false;
    }
    String answer = result.getAsString();
    return "0x".equals(answer);
  }
}
