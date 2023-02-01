package com.unstoppabledomains;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static final String TESTING_UNS_PROVIDER_URL = "https://goerli.infura.io/v3/e0c0cb9d12c440a29379df066de587e6";
    public static final String TESTING_ZNS_PROVIDER_URL = "https://dev-api.zilliqa.com";
    public static final String TESTING_UNS_L2_PROVIDER_URL = "https://polygon-mumbai.infura.io/v3/e0c0cb9d12c440a29379df066de587e6";

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
