package com.unstoppabledomains.config.network.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Location {
    String RegistryAddress;
    String ResolverAddress;
    Network NetworkId;
    String Blockchain;
    String Owner;
    String BlockchainProviderURL;
}
