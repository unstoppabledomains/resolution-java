package com.unstoppabledomains;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static final String TESTING_UNS_PROVIDER_URL = "https://eth-goerli.alchemyapi.io/v2/GBZTbTp1SUFwvUtVxVkKryzr3xbBq6k8";
    public static final String TESTING_ZNS_PROVIDER_URL = "https://dev-api.zilliqa.com";
    public static final String TESTING_UNS_L2_PROVIDER_URL = "https://polygon-mumbai.g.alchemy.com/v2/zvXnIKUcg-AuN67mMstCN_5ZsFZMf_j5";

    public static <T> void expectError(Callable<T> f, NSExceptionCode code) throws Exception {
        try {
            f.call();
            System.out.println("Didn't throw an " + code + " exception");
            fail();
        } catch (NamingServiceException e) {
            if (code != e.getCode()) {
                throw e;
            }
            assertEquals(code, e.getCode());
        }
    }

    public static <T> void expectError(Callable<T> f, NSExceptionCode code, Throwable cause) throws Exception {
        try {
            f.call();
            System.out.println("Didn't throw an " + code + " exception");
            fail();
        } catch (NamingServiceException e) {
            if (code != e.getCode()) {
                throw e;
            }
            if (cause != e.getCause()) {
                throw e;
            }
            assertEquals(code, e.getCode());
            assertEquals(cause, e.getCause());
        }
    }
}
