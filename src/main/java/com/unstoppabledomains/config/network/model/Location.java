package com.unstoppabledomains.config.network.model;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    String RegistryAddress;
    String ResolverAddress;
    Network NetworkId;
    String Blockchain;
    String Owner;
    String BlockchainProviderURL;

    private static final String addressFormat = "0x%040X";

    public Location() {}

    public void setRegistryAddress(String address) {
        this.RegistryAddress = address;
    }

    public void setRegistryAddress(BigInteger address) {
        this.RegistryAddress = String.format(addressFormat, address);
    }

    public void setResolverAddress(String address) {
        this.ResolverAddress = address;
    }

    public void setResolverAddress(BigInteger address) {
        this.ResolverAddress = String.format(addressFormat, address);
    }

    public void setOwnerAddress(BigInteger address) {
        this.Owner = String.format(addressFormat, address);
    }
}
