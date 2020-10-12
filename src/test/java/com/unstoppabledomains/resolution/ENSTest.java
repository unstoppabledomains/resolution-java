package com.unstoppabledomains.resolution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.unstoppabledomains.TestUtils;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NamingServiceException;

public class ENSTest {
  Resolution resolution;
  
  @BeforeEach
  public void initEach() {
    resolution = new Resolution("https://mainnet.infura.io/v3/781c1e5cae32417b93eac26042950d25");
  }

  @Test
  public void simpleTest() throws NamingServiceException {
    String owner = resolution.owner("monkybrain.eth");
    assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", owner);

    TestUtils.checkError(() -> resolution.email("monkybrain.eth"), NSExceptionCode.RecordNotFound);
  }

  @Test
  public void addressTest() throws NamingServiceException {
    String addr = resolution.addr("brad.eth", "eth");
    assertEquals("0x1af001667bb945d1bdbb05145eea7c21d86737f7", addr);
    
    TestUtils.checkError(() -> resolution.addr("brad.eth", "btc"), NSExceptionCode.UnsupportedCurrency);
    
    addr = resolution.addr("monkybrain.eth", "EtH");
    assertEquals("0x842f373409191cff2988a6f19ab9f605308ee462", addr);
   
    TestUtils.checkError(() -> resolution.addr("unregistered23.eth", "eth"), NSExceptionCode.UnregisteredDomain);
  }
}
