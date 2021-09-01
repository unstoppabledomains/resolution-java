package com.unstoppabledomains.config.network.model;

import java.util.List;

public class Contract {
    private String address;
    private List<String> legacyAddresses;
    private String deploymentBlock;

    private Contract() {
    }

    public String getAddress() {
        return address;
    }

    public List<String> getLegacyAddresses() {
        return legacyAddresses;
    }

    public String getDeploymentBlock() {
        return deploymentBlock;
    }
}
