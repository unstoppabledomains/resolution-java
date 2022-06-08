package com.unstoppabledomains;

import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.uns.ProxyReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyReaderTest {
    private static final String ADDRESS = "0x6fe7c857C1B0E54492C8762f27e0a45CA7ff264B";
    private static final String TOKEN_ID_HASH = "0x0df03d18a0a02673661da22d06f43801a986840e5812989139f0f7a2c41037c2";
    private static final BigInteger TOKEN_ID = new BigInteger(TOKEN_ID_HASH.replace("0x", ""), 16);

    private static ProxyReader proxyReaderContract;

    @BeforeAll
    public static void init() {
        proxyReaderContract = new ProxyReader(TestUtils.TESTING_UNS_L2_PROVIDER_URL, ADDRESS, new DefaultProvider());
    }

    @Test
    public void getOwner() throws Exception {
        String retrievedOwner = proxyReaderContract.getOwner(TOKEN_ID);

        assertEquals("0xd92d2a749424a5181ad7d45f786a9ffe46c10a7c", retrievedOwner);
    }

    @Test
    public void getRecord() throws Exception {
        String recordKey = "crypto.ETH.address";

        String retrievedRecord = proxyReaderContract.getRecord(recordKey, TOKEN_ID);

        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", retrievedRecord);
    }
}
