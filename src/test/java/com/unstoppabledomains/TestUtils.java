package com.unstoppabledomains;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Callable;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NamingServiceException;

public class TestUtils {
    public static void checkError(Callable<String> f, NSExceptionCode code) throws Exception {
        try {
           f.call();
           System.out.println("Didn't throw an " + code + " exception");
            fail();
        } catch(NamingServiceException e) {
            if (code != e.getCode()) {
                throw e;
            }
            assertEquals(code, e.getCode());
        }
    }
}