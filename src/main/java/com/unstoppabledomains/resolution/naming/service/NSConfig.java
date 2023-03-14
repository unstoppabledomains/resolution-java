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
    private String apiKey = null;

    NSConfig(Network chainId, String blockchainProviderUrl, String contractAddress) {
        this.chainId = chainId;
        this.blockchainProviderUrl = blockchainProviderUrl;
        this.contractAddress = contractAddress;
    }
}
