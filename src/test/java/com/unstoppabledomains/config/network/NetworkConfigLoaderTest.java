package com.unstoppabledomains.config.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.unstoppabledomains.config.client.Client;

public class NetworkConfigLoaderTest {

    @Test
    void shouldNotThrowExceptionsWhenLoadingNetworkConfig() {
        assertDoesNotThrow(NetworkConfigLoader::getNetworkConfig);
    }

    @Test
    void shouldLoadCorrectNetworkConfig() {
        String versionFromClient = Client.getVersion();
        // we want to keep this hardcoded and manually updated to test if the Client.getVersion correctly reads the .json file;
        String versionFromFile = "3.0.0";
        assertEquals(versionFromFile, versionFromClient);
    }
}
