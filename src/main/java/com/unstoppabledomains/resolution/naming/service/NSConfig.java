package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.config.network.model.Network;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NSConfig {
    private Network chainId;
    private String blockchainProviderUrl;
    private String contractAddress;
}
