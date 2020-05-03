package com.unstoppabledomains;

import org.junit.jupiter.api.Test;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.cns.Registry;

import org.junit.jupiter.api.BeforeEach;

public class ContractsTest {
  Registry registryContract;

  @BeforeEach
  public void initContracts() {
    try {
      registryContract = new Registry("0xD1E5b0FF1287aA9f9A268759062E4Ab08b9Dacbe");
    } catch (NamingServiceException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void fetchMethod() {
    registryContract.getResolver("0x4976fb03C32e5B8cfe2b6cCB31c09Ba78EBaBa41");
  }
}