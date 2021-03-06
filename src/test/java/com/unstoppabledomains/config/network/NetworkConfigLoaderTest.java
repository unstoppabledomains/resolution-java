package com.unstoppabledomains.config.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NetworkConfigLoaderTest {

    @Test
    void shouldNotThrowExceptionsWhenLoadingNetworkConfig() {
        assertDoesNotThrow(NetworkConfigLoader::getNetworkConfig);
    }
}
