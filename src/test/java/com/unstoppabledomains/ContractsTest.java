package com.unstoppabledomains;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.math.BigInteger;
import com.unstoppabledomains.resolution.contracts.cns.Registry;

import org.junit.jupiter.api.BeforeEach;

public class ContractsTest {
  Registry registryContract;

  @BeforeEach
  public void initContracts() {
    try {
      registryContract = new Registry("https://mainnet.infura.io/v3/213fff28936343858ca9c5115eff1419",
          "0xD1E5b0FF1287aA9f9A268759062E4Ab08b9Dacbe");
    } catch (Exception e) {
      e.printStackTrace();
      assertFalse(true);
    }
  }

  @Test
  public void fetchMethod() throws Exception {
    BigInteger tokenID = new BigInteger("0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9".replace("0x", ""), 16);
    String resolverAddr = registryContract.getResolver(tokenID);
    assertEquals("0xb66dce2da6afaaa98f2013446dbcb0f4b0ab2842", resolverAddr);
  }
}