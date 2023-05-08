package com.unstoppabledomains.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class KnownRecordsTest {

    @Test
    void shouldNotThrowExceptionsWhenLoadingRecords() {
        assertDoesNotThrow(KnownRecords::getRecordsObj);
    }

    @Test
    void shouldLoadCorrectRecords() {
        String versionFromCode = KnownRecords.getVersion();
        // we want to keep this hardcoded and manually updated to test if the Client.getVersion correctly reads the .json file;
        String versionFromFile = "2.1.26";
        assertEquals(versionFromFile, versionFromCode);
    }
}
