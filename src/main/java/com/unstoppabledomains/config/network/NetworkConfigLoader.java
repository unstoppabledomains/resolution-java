package com.unstoppabledomains.config.network;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.unstoppabledomains.config.network.model.Contract;
import com.unstoppabledomains.config.network.model.Contracts;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.config.network.model.NetworkConfig;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

public abstract class NetworkConfigLoader {

    private static final String CONFIG_FILE = "uns-config.json";

    private static final NetworkConfig NETWORK_CONFIG = initNetworkConfig();

    public static NetworkConfig getNetworkConfig() {
        return NETWORK_CONFIG;
    }

    public static String getContractAddress(Network chainId, String contractName) {
      final Contract contract = getContract(chainId, contractName);
      return contract.getAddress();
    }

    public static String getDeploymentBlock(Network chainId, String contractName) {
        final Contract contract = getContract(chainId, contractName);
        String deploymentBlock = contract.getDeploymentBlock();
        return deploymentBlock.equals("0x0") ? "earliest" : deploymentBlock; 
    }

    public static Contract getContract(Network chainId, String contractName) {
        final Map<Integer, Contracts> networks = NETWORK_CONFIG.getNetworks();

        final Contracts contracts = Optional.ofNullable(networks.get(chainId.getCode()))
                .orElseThrow(() -> new IllegalArgumentException("No contracts found for network: " + chainId));

        return Optional.ofNullable(contracts.getContracts().get(contractName))
                .orElseThrow(() -> new IllegalArgumentException("No contract found with name: " + contractName));
    }

    private static NetworkConfig initNetworkConfig() {
        NetworkConfig config;
        try {
            final JsonReader jsonReader =
                new JsonReader(new InputStreamReader(NetworkConfigLoader.class.getResourceAsStream(CONFIG_FILE)));
            config = new Gson().fromJson(jsonReader, NetworkConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load network config", e);
        }
        return config;
    }
}
