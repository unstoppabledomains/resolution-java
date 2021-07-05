package com.unstoppabledomains;

import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.uns.ProxyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyReaderTest {
    private static final String ADDRESS = "0x299974AeD8911bcbd2C61262605b89F591a53E83";
    private static final String TOKEN_NAME = "testing.crypto";
    private static final String TOKEN_ID_HASH = "0xd52e0f8bfe7e039fddb362c7e00f3628e2dca805f191d8bef74a07ca0e848245";
    private static final BigInteger TOKEN_ID = new BigInteger(TOKEN_ID_HASH.replace("0x", ""), 16);

    private static ProxyReader proxyReaderContract;

    @BeforeAll
    public static void init() {
        proxyReaderContract = new ProxyReader(TestUtils.TESTING_UNS_PROVIDER_URL, ADDRESS, new DefaultProvider());
    }

    @Test
    public void getOwner() throws Exception {
        String retrievedOwner = proxyReaderContract.getOwner(TOKEN_ID);

        assertEquals("0x58ca45e932a88b2e7d0130712b3aa9fb7c5781e2", retrievedOwner);
    }

    @Test
    public void getRecord() throws Exception {
        String recordKey = "crypto.ETH.address";

        String retrievedRecord = proxyReaderContract.getRecord(recordKey, TOKEN_ID);

        assertEquals("0x58cA45E932a88b2E7D0130712B3AA9fB7c5781e2", retrievedRecord);
    }
}
