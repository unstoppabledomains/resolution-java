package com.unstoppabledomains;

import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.cns.ProxyReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyReaderTest {
    private static final String ADDRESS = "0xa6E7cEf2EDDEA66352Fd68E5915b60BDbb7309f5";
    private static final String TOKEN_ID_HASH = "0x756e4e998dbffd803c21d23b06cd855cdc7a4b57706c95964a37e24b47c10fc9";
    private static final BigInteger TOKEN_ID = new BigInteger(TOKEN_ID_HASH.replace("0x", ""), 16);

    private static ProxyReader proxyReaderContract;

    @BeforeAll
    public static void init() {
        proxyReaderContract = new ProxyReader(TestUtils.TESTING_CNS_PROVIDER_URL, ADDRESS, new DefaultProvider());
    }

    @Test
    public void getOwner() throws Exception {
        String retrievedOwner = proxyReaderContract.getOwner(TOKEN_ID);

        assertEquals("0x8aad44321a86b170879d7a244c1e8d360c99dda8", retrievedOwner);
    }

    @Test
    public void getRecord() throws Exception {
        String recordKey = "crypto.ETH.address";

        String retrievedRecord = proxyReaderContract.getRecord(recordKey, TOKEN_ID);

        assertEquals("0x8aaD44321A86b170879d7A244c1e8d360c99DdA8", retrievedRecord);
    }
}
