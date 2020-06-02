package com.unstoppabledomains;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Callable;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NamingServiceException;

public class TestUtils {
    public static void checkError(Callable<String> f, NSExceptionCode code) {
        try {
           f.call();
           System.out.println("Didn't throw an " + code + " exception");
           assertTrue(false);
        } catch(NamingServiceException e) {
            assertEquals(code, e.getCode());
        } catch(Exception e) {
            System.out.println("Got an unexpected exception");
            e.printStackTrace();
            assertTrue(false);
        }
    }
}