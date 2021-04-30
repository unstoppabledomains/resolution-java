package com.unstoppabledomains.config.network;

import com.google.gson.Gson;
import com.unstoppabledomains.config.network.model.Contract;
import com.unstoppabledomains.config.network.model.Contracts;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.config.network.model.NetworkConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class NetworkConfigLoader {

    private static final String CONFIG_FILE = "network-config.json";

    private static final NetworkConfig NETWORK_CONFIG = initNetworkConfig();

    public static NetworkConfig getNetworkConfig() {
        return NETWORK_CONFIG;
    }

    public static String getContractAddress(Network chainId, String contractName) {
        final Map<Integer, Contracts> networks = NETWORK_CONFIG.getNetworks();

        final Contracts contracts = Optional.ofNullable(networks.get(chainId.getCode()))
                .orElseThrow(() -> new IllegalArgumentException("No contracts found for network: " + chainId));

        final Contract contract = Optional.ofNullable(contracts.getContracts().get(contractName))
                .orElseThrow(() -> new IllegalArgumentException("No contract found with name: " + contractName));

        return contract.getAddress();
    }

    private static NetworkConfig initNetworkConfig() {
        NetworkConfig config;
        try {
            final InputStreamReader reader = new InputStreamReader(NetworkConfigLoader.class.getResourceAsStream(CONFIG_FILE));
            final String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
            config = new Gson().fromJson(jsonString, NetworkConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load network config", e);
        }
        return config;
    }
}
