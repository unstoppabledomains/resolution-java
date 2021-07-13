package com.unstoppabledomains.resolution.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.esaulpaugh.headlong.abi.Tuple;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

class TestContract extends BaseContract {
  public TestContract(String url, String address, IProvider provider) {
    super("TEST", url, address, provider);
  }

  @Override
  protected String getAbiPath() {
    return "test-abi-path";
  }

  @Override
  protected JsonArray getAbi() {
    final String abi = "[{" + "\"inputs\": [" + "{" + "\"indexed\": true," + "\"internalType\": \"uint256\","
        + "\"name\": \"id\"," + "\"type\": \"uint256\"" + "},{" + "\"indexed\": false,"
        + "\"internalType\": \"string\"," + "\"name\": \"value\"," + "\"type\": \"string\"" + "}],"
        + "\"name\": \"testEvent\"," + "\"type\": \"event\"" + "},{" + "\"inputs\": ["
        + "  { \"internalType\": \"uint256\", \"name\": \"valueId\", \"type\": \"uint256\" }" + "],"
        + "\"name\": \"getValue\"," + "\"outputs\": ["
        + "  { \"internalType\": \"string\", \"name\": \"value\", \"type\": \"string\" }" + "],"
        + "\"stateMutability\": \"view\"," + " \"type\": \"function\"" + "}]";
    return new Gson().fromJson(abi, JsonArray.class);
  }

  public <T> T fetchOne(String method, Object[] args) throws NamingServiceException {
    return super.fetchOne(method, args);
  }

  public List<Tuple> fetchLogs(String fromBlock, String eventName, String[] topics) throws NamingServiceException {
    return super.fetchLogs(fromBlock, eventName, topics);
  }
}

@ExtendWith(MockitoExtension.class)
public class BaseContractTest {
  private static TestContract testContract;
  private static final String TEST_URL = "http://test-url.example.com";
  private static final String ETH_CALL_PARAM = "{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"method\":\"eth_call\","
      + "\"params\":[{" + "\"data\":\"0x0ff4c9160000000000000000000000000000000000000000000000000000000000000001\","
      + "\"to\":\"test-address\"}," + "\"latest\"]}";

  private static final String ETH_GET_LOGS_PARAM = "{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"method\":\"eth_getLogs\","
      + "\"params\":[{" + "\"fromBlock\":\"earliest\"," + "\"toBlock\":\"latest\"," + "\"address\":\"test-address\","
      + "\"topics\":[\"0x2bc13f2ba6f4932aee041bb50f835e6392611e22122e6674651ed9ad7a21721a\",\"0x0000000000000000000000000000000000000000000000000000000000000001\"]"
      + "}]}";

  @Mock
  private static IProvider mockProvider;

  @BeforeEach
  public void setup() {
    testContract = new TestContract(TEST_URL, "test-address", mockProvider);
  }

  @Test
  public void testBaseContractFetchOne() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_CALL_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1,"
        + "\"result\":\"0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000011746573742072657475726e2076616c7565000000000000000000000000000000\"}",
        JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    String result = testContract.fetchOne("getValue", new Object[] { BigInteger.valueOf(1L) });

    assertEquals("test return value", result);
  }

  @Test
  public void testBaseContractFetchOneContractError() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_CALL_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"error\": {"
        + "\"code\": -32000," + "\"message\": \"execution reverted\"" + "}}", JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    String result = testContract.fetchOne("getValue", new Object[] { BigInteger.valueOf(1L) });

    assertEquals(null, result);
  }

  @Test
  public void testBaseContractFetchOneNetworkError() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_CALL_PARAM, JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenThrow(new IOException("Network is down"));

    assertThrows(NamingServiceException.class,
        () -> testContract.fetchOne("getValue", new Object[] { BigInteger.valueOf(1L) }));
  }

  @Test
  public void testBaseContractFetchLogs() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_GET_LOGS_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"result\":[{"
        + "\"address\": \"0x249f2ca1d033fd8f0272bdd0706193e4e3bff84b\","
        + "\"blockHash\": \"0x24c8d3a6b9cc2837b95f6dea9a64661136d31eef5d183f8639bfe7209f8ab1b4\","
        + "\"blockNumber\": \"0x87cfe0\","
        + "\"data\": \"0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000011746573742072657475726e2076616c7565000000000000000000000000000000\","
        + "\"logIndex\": \"0xa\"," + "\"removed\": false," + "\"topics\": ["
        + "\"0x2bc13f2ba6f4932aee041bb50f835e6392611e22122e6674651ed9ad7a21721a\","
        + "\"0x0000000000000000000000000000000000000000000000000000000000000001\"" + "],"
        + "\"transactionHash\": \"0x127d129c91349a69b699d0995ac793f0908412fe2131a301fdbd9e4024f979ec\","
        + "\"transactionIndex\": \"0x9\"" + "}]}", JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    List<Tuple> results = testContract.fetchLogs("earliest", "testEvent",
        new String[] { "0x0000000000000000000000000000000000000000000000000000000000000001" });

    assertEquals(results.size(), 1);
    Tuple log = results.get(0);
    String value = (String) log.get(0);
    assertEquals("test return value", value);
  }

  @Test
  public void testBaseContractFetchMultipleLogs() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_GET_LOGS_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"result\":[{"
        + "\"address\": \"0x249f2ca1d033fd8f0272bdd0706193e4e3bff84b\","
        + "\"blockHash\": \"0x24c8d3a6b9cc2837b95f6dea9a64661136d31eef5d183f8639bfe7209f8ab1b4\","
        + "\"blockNumber\": \"0x87cfe0\","
        + "\"data\": \"0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000011746573742072657475726e2076616c7565000000000000000000000000000000\","
        + "\"logIndex\": \"0xa\"," + "\"removed\": false," + "\"topics\": ["
        + "\"0x2bc13f2ba6f4932aee041bb50f835e6392611e22122e6674651ed9ad7a21721a\","
        + "\"0x0000000000000000000000000000000000000000000000000000000000000001\"" + "],"
        + "\"transactionHash\": \"0x127d129c91349a69b699d0995ac793f0908412fe2131a301fdbd9e4024f979ec\","
        + "\"transactionIndex\": \"0x9\"" + "},{" + "\"address\": \"0x249f2ca1d033fd8f0272bdd0706193e4e3bff84b\","
        + "\"blockHash\": \"0x276031ef6a6df0d178db1f27aaf6bd3214c824c37c07d69a7598b882d258e06f\","
        + "\"blockNumber\": \"0x87d07c\","
        + "\"data\": \"0x0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001174657374207365636f6e642076616c7565000000000000000000000000000000\","
        + "\"logIndex\": \"0x13\"," + "\"removed\": false," + "\"topics\": ["
        + "    \"0x2bc13f2ba6f4932aee041bb50f835e6392611e22122e6674651ed9ad7a21721a\","
        + "    \"0x0000000000000000000000000000000000000000000000000000000000000001\"" + "],"
        + "\"transactionHash\": \"0xadb24a3b36516fe9fa8bb29ac77faef2d0ec2010c6b6a7c9d58cca63385b2998\","
        + "\"transactionIndex\": \"0x14\"" + "}]}", JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    List<Tuple> results = testContract.fetchLogs("earliest", "testEvent",
        new String[] { "0x0000000000000000000000000000000000000000000000000000000000000001" });

    assertEquals(results.size(), 2);
    String firstValue = (String) results.get(0).get(0);
    assertEquals("test return value", firstValue);
    String secondValue = (String) results.get(1).get(0);
    assertEquals("test second value", secondValue);
  }

  @Test
  public void testBaseContractFetchLogsContractError() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_GET_LOGS_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"error\": {"
        + "\"code\": -32000," + "\"message\": \"execution reverted\"" + "}}", JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    List<Tuple> results = testContract.fetchLogs("earliest", "testEvent",
        new String[] { "0x0000000000000000000000000000000000000000000000000000000000000001" });

    assertEquals(results.size(), 0);
  }

  @Test
  public void testBaseContractFetchLogsNetworkError() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_GET_LOGS_PARAM, JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenThrow(new IOException());

    assertThrows(NamingServiceException.class, () -> testContract.fetchLogs("earliest", "testEvent",
        new String[] { "0x0000000000000000000000000000000000000000000000000000000000000001" }));
  }
}
