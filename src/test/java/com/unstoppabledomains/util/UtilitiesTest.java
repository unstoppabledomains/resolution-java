package com.unstoppabledomains.util;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UtilitiesTest {
  private class TestData {
    private String domainName;
    private BigInteger tokenId;
    private String namehash;

    public TestData(String domainName, BigInteger tokenId, String namehash) {
      this.domainName = domainName;
      this.tokenId = tokenId;
      this.namehash = namehash;
    }
  }

  private List<TestData> domains = new ArrayList<>(Arrays.asList(
    new TestData(
      "brad.crypto", 
      new BigInteger("53115498937382692782103703677178119840631903773202805882273058578308100329417"), 
      "0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9"),
    new TestData(
      "mydomain.crypto", 
      new BigInteger("91589012262240444134498651412621473912117251166174604400624177990187901599677"), 
      "0xca7d8b3c2f543d3f4487f067e15a5ba32e7ef84fddbf42697b9aa546ed213fbd"),
      new TestData(
        "example.crypto",
        new BigInteger("96577222378083847444162770147739473281446101124661305882016276084537676397438"),
        "0xd584c5509c6788ad9d9491be8ba8b4422d05caf62674a98fbf8a9988eeadfb7e"),
      new TestData(
        "fake domain with lots of zeros",
        new BigInteger("11"),
        "0x000000000000000000000000000000000000000000000000000000000000000b")
  ));

  @TestFactory
  public Iterator<DynamicTest> testTokenIDToNamehash() {
    return domains.stream().map(domainData -> DynamicTest.dynamicTest("Test tokenId to namehash for " + domainData.domainName, () -> {
      String namehash = Utilities.tokenIDToNamehash(domainData.tokenId);
      assertEquals(domainData.namehash, namehash);
    })).iterator();
  }
  
  @TestFactory
  public Iterator<DynamicTest> testNamehashToTokenID() {
    return domains.stream().map(domainData -> DynamicTest.dynamicTest("Test namehash to tokenId for " + domainData.domainName, () -> {
      BigInteger tokenId = Utilities.namehashToTokenID(domainData.namehash);
      assertEquals(domainData.tokenId, tokenId);
    })).iterator();
  }
}
