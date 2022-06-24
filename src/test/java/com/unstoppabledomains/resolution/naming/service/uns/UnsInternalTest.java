package com.unstoppabledomains.resolution.naming.service.uns;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unstoppabledomains.TestUtils;
import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.naming.service.NSConfig;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UnsInternalTest {
  
  private static UNSInternal layer1;
  private static UNSInternal layer2;

  @BeforeAll
  public static void init() {
      layer1 = new UNSInternal(
        UNSLocation.Layer1,
        new NSConfig(
          Network.GOERLI,
          TestUtils.TESTING_UNS_PROVIDER_URL,
          NetworkConfigLoader.getContractAddress(Network.GOERLI, "ProxyReader")
        ),
        new DefaultProvider()
      );

      layer2 =  new UNSInternal(
        UNSLocation.Layer2,
        new NSConfig(
          Network.MUMBAI_TESTNET,
          TestUtils.TESTING_UNS_L2_PROVIDER_URL,
          NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader")
        ),
        new DefaultProvider()
      );
  }

  @Test
  public void testGetRecordsOnLayer1() throws Exception {
    Map<String, String> given = new HashMap<String, String>() {{
      put("crypto.ETH.address", "0x084Ac37CDEfE1d3b68a63c08B203EFc3ccAB9742");
  }};
    List<String> recordsKeys = new ArrayList<String>(given.keySet());
    Map<String, String> result = layer1.getRecords("reseller-test-udtesting-459239285.crypto", recordsKeys);
    assertEquals(result.size(), recordsKeys.size());
    for (Map.Entry<String, String> entry: given.entrySet()) {
          String key = entry.getKey();
          assertEquals(result.get(key), entry.getValue());
      }
  }

  @Test
  public void testGetRecordsOnLayer2() throws Exception {
    Map<String, String> given = new HashMap<String, String>() {{
      put("crypto.ETH.address", "0xe7474D07fD2FA286e7e0aa23cd107F8379085037");
      put("crypto.BTC.address", "");
      put("custom.record", "custom.value");
      put("unknown.record", "");
    }};

    List<String> recordsKeys = new ArrayList<String>(given.keySet());
    Map<String, String> result = layer2.getRecords("udtestdev-johnnytest.wallet", recordsKeys);
    assertEquals(result.size(), recordsKeys.size());
    for (Map.Entry<String, String> entry: given.entrySet() ) {
      String key = entry.getKey();
      assertEquals(result.get(key), entry.getValue());
    }
  }

  @Test
  public void testGetRecordsUnregisteredError() throws Exception {
    TestUtils.expectError(() -> layer2.getRecords("testing.crypto", Arrays.asList("crypto.ETH.address")), NSExceptionCode.UnregisteredDomain);
    TestUtils.expectError(() -> layer1.getRecords("unregistered.crypto", Arrays.asList("crypto.ETH.address")), NSExceptionCode.UnregisteredDomain);
  }
}
