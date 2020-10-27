package com.unstoppabledomains.config.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unstoppabledomains.config.network.model.NetworkConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class NetworkConfigLoader {

    private static final String CONFIG_FILE = "network-config.json";

    private static final NetworkConfig NETWORK_CONFIG = initNetworkConfig();

    public static NetworkConfig getNetworkConfig() {
        return NETWORK_CONFIG;
    }

    private static NetworkConfig initNetworkConfig() {
        NetworkConfig config;
        try {
            final InputStreamReader reader = new InputStreamReader(NetworkConfigLoader.class.getResourceAsStream(CONFIG_FILE));
            final String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

            config = new ObjectMapper().readValue(jsonString, NetworkConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load network config", e);
        }
        return config;
    }
}
