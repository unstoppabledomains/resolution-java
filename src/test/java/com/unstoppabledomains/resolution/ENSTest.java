package com.unstoppabledomains.resolution;

import com.unstoppabledomains.TestUtils;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.List;

public class ENSTest {
  private static DomainResolution resolution;

  @BeforeAll
  public static void init() {
    resolution = new Resolution();
  }

  @Test
  public void ownerTest() throws NamingServiceException {
    String owner = resolution.getOwner("monkybrain.eth");
    assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", owner);
  }

  @Test
  public void errorTest() throws Exception {
    TestUtils.expectError(() -> resolution.getEmail("monkybrain.eth"), NSExceptionCode.RecordNotFound);
    TestUtils.expectError(() -> resolution.getAddress("brad.eth", "btc"), NSExceptionCode.UnsupportedCurrency);
    TestUtils.expectError(() -> resolution.getAddress("unregistered23.eth", "eth"), NSExceptionCode.UnregisteredDomain);
    TestUtils.expectError(() -> resolution.getTokenURI("brad.eth"), NSExceptionCode.NotImplemented);
    TestUtils.expectError(() -> resolution.unhash("0x062c59dccddeb7f5b0f32f3a0ded53b33f90b5ae8ddcc681f4ac5048ab5045da", NamingServiceType.ENS), NSExceptionCode.NotImplemented);
  }

  @Test
  public void addressTest() throws NamingServiceException {
    String addr = resolution.getAddress("brad.eth", "eth");
    assertEquals("0x1af001667bb945d1bdbb05145eea7c21d86737f7", addr);

    addr = resolution.getAddress("monkybrain.eth", "EtH");
    assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", addr);
  }

  @Test
  public void batchOwnersTest() throws NamingServiceException {
    List<String> domains = Arrays.asList("brad.eth", "monkybrain.eth", "udtestdevnotexist.kred", "matthewgould.eth", "testthing.eth");
    List<String> owners = resolution.getBatchOwners(domains);
    String[] correctOwnerAddresses = { "0x1af001667bb945d1bdbb05145eea7c21d86737f7",  "0x842f373409191cff2988a6f19ab9f605308ee462", null, "0x714ef33943d925731fbb89c99af5780d888bd106", "0x904dac3347ea47d208f3fd67402d039a3b99859"};
    assertArrayEquals(owners.toArray(), correctOwnerAddresses);
  }

  @Test
  public void batchOwnerOverflowTest() throws Exception {
    String[] domains = new String[300];
    Arrays.fill(domains, "somedomain.eth");
    TestUtils.expectError(() -> resolution.getBatchOwners(Arrays.asList(domains)), NSExceptionCode.MaxThreadLimit);
  }
}