package com.unstoppabledomains.config.network.model;

import java.util.List;

public class Contract {
    private String address;
    private List<String> legacyAddresses;

    private Contract() {
    }

    public String getAddress() {
        return address;
    }

    public List<String> getLegacyAddresses() {
        return legacyAddresses;
    }
}
