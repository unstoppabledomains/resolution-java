package com.unstoppabledomains.resolution.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
        + "\"stateMutability\": \"view\"," + " \"type\": \"function\"" + "},"
        + "{" + "\"inputs\": ["
        + "  { \"internalType\": \"bytes[]\", \"name\": \"data\", \"type\": \"bytes[]\" }" + "],"
        + "\"name\": \"multicall\"," + "\"outputs\": ["
        + "  { \"internalType\": \"bytes[]\", \"name\": \"results\", \"type\": \"bytes[]\" }" + "],"
        + "\"stateMutability\": \"nonpayable\"," + " \"type\": \"function\"" + "}]";
    return new Gson().fromJson(abi, JsonArray.class);
  }

  public <T> T fetchOne(String method, Object[] args) throws NamingServiceException {
    return super.fetchOne(method, args);
  }
}

@ExtendWith(MockitoExtension.class)
public class BaseContractTest {
  private static TestContract testContract;
  private static final String TEST_URL = "http://test-url.example.com";
  private static final String ETH_CALL_PARAM = "{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"method\":\"eth_call\","
      + "\"params\":[{" + "\"data\":\"0x0ff4c9160000000000000000000000000000000000000000000000000000000000000001\","
      + "\"to\":\"test-address\"}," + "\"latest\"]}";

  private static final String ETH_MULTICALL_PARAM = "{\"jsonrpc\":\"2.0\"," + "\"id\":1," + "\"method\":\"eth_call\","
  + "\"params\":[{" + "\"data\":\"0xac9650d800000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000a000000000000000000000000000000000000000000000000000000000000000240ff4c91600000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000240ff4c916000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000\","
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
  public void testBaseContractFetchMulticall() throws Exception {
    JsonObject paramObject = new Gson().fromJson(ETH_MULTICALL_PARAM, JsonObject.class);
    JsonObject returnObject = new Gson().fromJson("{\"jsonrpc\":\"2.0\"," + "\"id\":1,"
        + "\"result\":\"0x00000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000c000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000a746573742076616c75650000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000600000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c746573742076616c756520320000000000000000000000000000000000000000\"}",
        JsonObject.class);
    when(mockProvider.request(eq(TEST_URL), eq(paramObject))).thenReturn(returnObject);

    List<BaseContract.MulticallArgs> args = new ArrayList<>();
    args.add(testContract.new MulticallArgs("getValue", new Object[] { BigInteger.valueOf(1L) }));
    args.add(testContract.new MulticallArgs("getValue", new Object[] { BigInteger.valueOf(2L) }));
    List<Tuple> result = testContract.fetchMulticall(args);
    
    assertEquals("test value", result.get(0).get(0));
    assertEquals("test value 2", result.get(1).get(0));
  }
}
