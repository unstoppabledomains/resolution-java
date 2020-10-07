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
    String ipfs = resolution.ipfsHash("crunk.eth");
    assertEquals( "0x7e1d12f34e038a2bda3d5f6ee0809d72f668c357d9e64fd7f622513f06ea652146ab5fdee35dc4ce77f1c089fd74972691fccd48130306d9eafcc6e1437d1ab21b", ipfs);
  }
}
