package com.unstoppabledomains.config.network.model;

import java.util.Map;

public class NetworkConfig {
    private String version;
    private Map<Integer, Contracts> networks;

    private NetworkConfig() {
    }

    public String getVersion() {
        return version;
    }

    public Map<Integer, Contracts> getNetworks() {
        return networks;
    }
}
