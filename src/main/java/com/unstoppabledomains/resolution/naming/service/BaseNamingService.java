package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.config.network.model.Network;

public abstract class BaseNamingService implements NamingService {
    protected Network chainId;
    protected String blockchainProviderUrl;

    protected BaseNamingService(NSConfig nsConfig) {
        this.chainId = nsConfig.getChainId();
        this.blockchainProviderUrl = nsConfig.getBlockchainProviderUrl();
    }
}
