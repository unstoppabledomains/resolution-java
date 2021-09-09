package com.unstoppabledomains.resolution;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.naming.service.ENS;
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.ZNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNSConfig;
import com.unstoppabledomains.resolution.naming.service.uns.UNSLocation;
import com.unstoppabledomains.util.BuilderNSConfig;

public class ResolutionBuilder {
    private static final String INFURA_URL = "https://%s.infura.io/v3/%s";
    static final String ENS_DEFAULT_URL = "https://mainnet.infura.io/v3/d423cf2499584d7fbe171e33b42cfbee";
    static final String UNS_DEFAULT_URL = "https://mainnet.infura.io/v3/e0c0cb9d12c440a29379df066de587e6";
    static final String UNS_L2_DEFAULT_URL = "https://polygon-mumbai.infura.io/v3/e0c0cb9d12c440a29379df066de587e6";
    static final String ZILLIQA_DEFAULT_URL = "https://api.zilliqa.com";

    static final String ENS_DEFAULT_REGISTRY_ADDRESS = "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e";
    static final String ZNS_DEFAULT_REGISTRY_ADDRESS = "0x9611c53BE6d1b32058b2747bdeCECed7e1216793";

    private final Map<NamingServiceType, BuilderNSConfig> serviceConfigs;
    private final Map<UNSLocation, BuilderNSConfig> unsConfigs;
    private IProvider provider;
    private Resolution.ResolutionBuilderConnector connector;

    public ResolutionBuilder(Resolution.ResolutionBuilderConnector connector) {
        this.connector = connector;
        serviceConfigs = new HashMap<>();
        serviceConfigs.put(NamingServiceType.ZNS, new BuilderNSConfig(Network.MAINNET, ZILLIQA_DEFAULT_URL, ZNS_DEFAULT_REGISTRY_ADDRESS));
        serviceConfigs.put(NamingServiceType.ENS, new BuilderNSConfig(Network.MAINNET, ENS_DEFAULT_URL, ENS_DEFAULT_REGISTRY_ADDRESS));
        
        String unsProxyAddress = NetworkConfigLoader.getContractAddress(Network.MAINNET, "ProxyReader");
        String unsl2ProxyAddress = NetworkConfigLoader.getContractAddress(Network.MUMBAI_TESTNET, "ProxyReader");
        unsConfigs = new HashMap<>();
        unsConfigs.put(UNSLocation.Layer1, new BuilderNSConfig(Network.MAINNET, UNS_DEFAULT_URL, unsProxyAddress));
        unsConfigs.put(UNSLocation.Layer2, new BuilderNSConfig(Network.MUMBAI_TESTNET, UNS_L2_DEFAULT_URL, unsl2ProxyAddress));
        
        provider = new DefaultProvider();
    }

    /**
     * @param nsType  the naming service for which config is applied
     * @param chainId blockchain network ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder chainId(NamingServiceType nsType, Network chainId) {
        NSConfig nsConfig = serviceConfigs.get(nsType);
        nsConfig.setChainId(chainId);
        return this;
    }

    /**
     * Applies blockchain network ID for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param chainId blockchain network ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder chainId(UNSLocation location, Network chainId) {
        NSConfig nsConfig = unsConfigs.get(location);
        nsConfig.setChainId(chainId);
        return this;
    }

    /**
     * @param nsType      the naming service for which config is applied
     * @param providerUrl blockchain provider URL
     * @return builder object to allow chaining
     */
    public ResolutionBuilder providerUrl(NamingServiceType nsType, String providerUrl) {
        NSConfig nsConfig = serviceConfigs.get(nsType);
        return this.providerUrl(nsConfig, providerUrl);
    }

    /**
     * Applies the blockchain provider URL for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param providerUrl blockchain provider URL
     * @return builder object to allow chaining
     */
    public ResolutionBuilder providerUrl(UNSLocation location, String providerUrl) {
        NSConfig nsConfig = unsConfigs.get(location);
        return this.providerUrl(nsConfig, providerUrl);
    }

    private ResolutionBuilder providerUrl(NSConfig nsConfig, String providerUrl) {
        Network chainId = getNetworkId(providerUrl);
        if (chainId == null) {
            chainId = nsConfig.getChainId();
        }
        nsConfig.setBlockchainProviderUrl(providerUrl);
        nsConfig.setChainId(chainId);
        return this;
    }

    /**
     * @param nsType            the naming service for which config is applied
     * @param contractAddress   address of `Registry` contract for ZNS or ENS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder contractAddress(NamingServiceType nsType, String contractAddress) {
        NSConfig nsConfig = serviceConfigs.get(nsType);
        nsConfig.setContractAddress(contractAddress);
        return this;
    }

    /**
     * Applies the address of `ProxyReader` contract for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param contractAddress   address of `ProxyReader` contract for UNS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder contractAddress(UNSLocation location, String contractAddress) {
        NSConfig nsConfig = unsConfigs.get(location);
        nsConfig.setContractAddress(contractAddress);
        return this;
    }

    /**
     * Configuration for <a href="https://infura.io">infura.io</a>
     * blockchain provider with previously set network ID using
     * {@link ResolutionBuilder#chainId(NamingServiceType, Network)} or default one
     * ({@link Network#MAINNET})
     *
     * @param nsType    the naming service for which config is applied
     * @param projectId Infura project ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder infura(NamingServiceType nsType, String projectId) {
        NSConfig nsConfig = serviceConfigs.get(nsType);
        final Network network = nsConfig.getChainId();
        return this.infura(nsConfig, network, projectId);
    }

    /**
     * Configuration for <a href="https://infura.io">infura.io</a>
     * blockchain provider with previously set network ID using
     * {@link ResolutionBuilder#chainId(NamingServiceType, Network)} or default one
     * ({@link Network#MAINNET})
     *
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param projectId Infura project ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder infura(UNSLocation location, String projectId) {
        NSConfig nsConfig = unsConfigs.get(location);
        final Network network = nsConfig.getChainId();
        return this.infura(nsConfig, network, projectId);
    }

    /**
     * Configuration for <a href="https://infura.io">infura</a> blockchain provider
     *
     * @param nsType    the naming service for which config is applied
     * @param chainId   blockchain network ID
     * @param projectId Infura project ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder infura(NamingServiceType nsType, Network chainId, String projectId) {
        NSConfig nsConfig = serviceConfigs.get(nsType);
        return this.infura(nsConfig, chainId, projectId);
    }

    /**
     * Configuration for <a href="https://infura.io">infura</a> blockchain provider
     *
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param chainId   blockchain network ID
     * @param projectId Infura project ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder infura(UNSLocation location, Network chainId, String projectId) {
        NSConfig nsConfig = unsConfigs.get(location);
        return this.infura(nsConfig, chainId, projectId);
    }

    private ResolutionBuilder infura(NSConfig nsConfig, Network chainId, String projectId) {
        nsConfig.setChainId(chainId);

        String infuraUrl = String.format(INFURA_URL, chainId.getName(), projectId);
        nsConfig.setBlockchainProviderUrl(infuraUrl);

        return this;
    }

    /**
     * Configuration for provider interface, use if needed to take control over network calls
     * @param provider must implement IProvider interface
     * @return builder object to allow chaining
     */
    public ResolutionBuilder provider(IProvider provider) {
        setProvider(provider);
        return this;
    } 


    private <T extends Enum<T>> void checkConfigs(Map<T, BuilderNSConfig> configs, String messagePrefix) {
        for (Entry<T, BuilderNSConfig> config : configs.entrySet()) {
            if (!config.getValue().isConfigured()) {
                throw new IllegalArgumentException(messagePrefix + " " + config.getKey().name() + ": " + config.getValue().getMisconfiguredMessage());
            }
        }
        configs.forEach((l, c) -> {
        });
    }

    /**
     * Call directly to get default configs or override them with {@link ResolutionBuilder} methods
     *
     * @return resolution object
     */
    public Resolution build() {
        checkConfigs(unsConfigs, "Invalid configuration for UNS layer");
        checkConfigs(serviceConfigs, "Invalid configuration for service");

        Map<NamingServiceType, NamingService> services = new HashMap<>();
        services.put(NamingServiceType.UNS, new UNS(new UNSConfig(unsConfigs.get(UNSLocation.Layer1),
                                                              unsConfigs.get(UNSLocation.Layer2)), provider));
        services.put(NamingServiceType.ZNS, new ZNS(serviceConfigs.get(NamingServiceType.ZNS), provider));
        services.put(NamingServiceType.ENS, new ENS(serviceConfigs.get(NamingServiceType.ENS), provider));
        return connector.buildResolution(services);
    }

    private void setProvider(IProvider provider) {
        this.provider = provider;
    }

    /**
     * Makes a call via provider to the blockchainProviderUrl and returns the networkId
     * @param blockchainProviderUrl RPC endpoint url
     * @return Network object or null if couldn't retrive the network
     */
    private Network getNetworkId(String blockchainProviderUrl) {
        JsonObject body = new JsonObject();
        body.addProperty("jsonrpc", "2.0");
        body.addProperty("method", "net_version");
        body.add("params", new JsonArray());
        body.addProperty("id", 67);
        try {
            JsonObject response = provider.request(blockchainProviderUrl, body);
            return Network.getNetwork(response.get("result").getAsInt());
        } catch(Exception e) {
            return null;
        }
    }
}
