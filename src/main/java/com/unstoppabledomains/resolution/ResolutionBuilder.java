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
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.ZNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNSConfig;
import com.unstoppabledomains.resolution.naming.service.uns.UNSLocation;
import com.unstoppabledomains.util.BuilderNSConfig;

public class ResolutionBuilder {
    static final String ZILLIQA_DEFAULT_URL = "https://api.zilliqa.com";
    static final String UD_RPC_PROXY_BASE_URL = "https://api.unstoppabledomains.com/resolve";

    static final String ZNS_DEFAULT_REGISTRY_ADDRESS = "0x9611c53BE6d1b32058b2747bdeCECed7e1216793";

    private final Map<NamingServiceType, BuilderNSConfig> serviceConfigs;
    private final Map<UNSLocation, BuilderNSConfig> unsConfigs;
    private IProvider provider;
    private Resolution.ResolutionBuilderConnector connector;

    public ResolutionBuilder(Resolution.ResolutionBuilderConnector connector) {
        this.connector = connector;
        serviceConfigs = new HashMap<>();
        serviceConfigs.put(NamingServiceType.ZNS, new BuilderNSConfig(Network.MAINNET, ZILLIQA_DEFAULT_URL, ZNS_DEFAULT_REGISTRY_ADDRESS));
        
        String unsProxyAddress = NetworkConfigLoader.getContractAddress(Network.MAINNET, "ProxyReader");
        String unsl2ProxyAddress = NetworkConfigLoader.getContractAddress(Network.MATIC_MAINNET, "ProxyReader");
        unsConfigs = new HashMap<>();
        unsConfigs.put(UNSLocation.Layer1, new BuilderNSConfig(Network.MAINNET, null, unsProxyAddress));
        unsConfigs.put(UNSLocation.Layer2, new BuilderNSConfig(Network.MATIC_MAINNET, null, unsl2ProxyAddress));
        
        provider = new DefaultProvider();
    }

    /**
     * @param chainId blockchain network ID for ZNS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder znsChainId(Network chainId) {
        NSConfig nsConfig = serviceConfigs.get(NamingServiceType.ZNS);
        nsConfig.setChainId(chainId);
        return this;
    }

    /**
     * Applies blockchain network ID for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param chainId blockchain network ID
     * @return builder object to allow chaining
     */
    public ResolutionBuilder unsChainId(UNSLocation location, Network chainId) {
        NSConfig nsConfig = unsConfigs.get(location);
        nsConfig.setChainId(chainId);
        return this;
    }

    /**
     * @param providerUrl blockchain provider URL for ZNS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder znsProviderUrl( String providerUrl) {
        NSConfig nsConfig = serviceConfigs.get(NamingServiceType.ZNS);
        return this.providerUrl(nsConfig, providerUrl, false);
    }

    /**
     * Applies the blockchain provider URL for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param providerUrl blockchain provider URL
     * @return builder object to allow chaining
     */
    public ResolutionBuilder unsProviderUrl(UNSLocation location, String providerUrl) {
        NSConfig nsConfig = unsConfigs.get(location);
        return this.providerUrl(nsConfig, providerUrl, false);
    }

    public ResolutionBuilder udUnsClient(String apiKey) {
        NSConfig l1NsConfig = unsConfigs.get(UNSLocation.Layer1);
        l1NsConfig.setChainId(Network.MAINNET);

        NSConfig l2NsConfig = unsConfigs.get(UNSLocation.Layer2);
        l2NsConfig.setChainId(Network.MATIC_MAINNET);

        this.provider.setHeader("Authorization", "Bearer " + apiKey);
        this.provider.setHeader("X-Lib-Agent", DefaultProvider.getUserAgent());

        this.providerUrl(l1NsConfig, ResolutionBuilder.UD_RPC_PROXY_BASE_URL + "/chains/eth/rpc", true);
        this.providerUrl(l2NsConfig, ResolutionBuilder.UD_RPC_PROXY_BASE_URL + "/chains/matic/rpc", true);

        return this;
    }

    private ResolutionBuilder providerUrl(NSConfig nsConfig, String providerUrl, Boolean shouldForwardError) {
        Network chainId = getNetworkId(providerUrl, shouldForwardError);
        if (chainId == null) {
            chainId = nsConfig.getChainId();
        }
        nsConfig.setBlockchainProviderUrl(providerUrl);
        nsConfig.setChainId(chainId);

        return this;
    }

    /**
     * @param contractAddress   address of `Registry` contract for ZNS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder znsContractAddress(String contractAddress) {
        NSConfig nsConfig = serviceConfigs.get(NamingServiceType.ZNS);
        nsConfig.setContractAddress(contractAddress);
        return this;
    }

    /**
     * Applies the address of `ProxyReader` contract for UNS naming service 
     * @param location the location of the UNS service (layer 1 or layer 2)
     * @param contractAddress   address of `ProxyReader` contract for UNS
     * @return builder object to allow chaining
     */
    public ResolutionBuilder unsContractAddress(UNSLocation location, String contractAddress) {
        NSConfig nsConfig = unsConfigs.get(location);
        nsConfig.setContractAddress(contractAddress);
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


    private <T extends Enum<T>> void checkConfigs(Map<T, BuilderNSConfig> configs, String messagePrefix) throws IllegalArgumentException{
        for (Entry<T, BuilderNSConfig> config : configs.entrySet()) {

            if (!config.getValue().isConfigured()) {
                throw new IllegalArgumentException(messagePrefix + " " + config.getKey().name() + ": " + config.getValue().getMisconfiguredMessage());
            }
        }
    }

    /**
     * Call directly to get default configs or override them with {@link ResolutionBuilder} methods
     *
     * @return resolution object
     */
    public Resolution build() throws IllegalArgumentException {
        if (unsConfigs.get(UNSLocation.Layer1).isDefault()) {
            throw new IllegalArgumentException("Configuration should be provided for UNS Layer1");
        }

        if (unsConfigs.get(UNSLocation.Layer2).isDefault()) {
            throw new IllegalArgumentException("Configuration should be provided for UNS Layer2");
        }

        checkConfigs(unsConfigs, "Invalid configuration for UNS layer");
        checkConfigs(serviceConfigs, "Invalid configuration for service");

        Map<NamingServiceType, NamingService> services = new HashMap<>();
        services.put(NamingServiceType.UNS, new UNS(new UNSConfig(unsConfigs.get(UNSLocation.Layer1),
                                                              unsConfigs.get(UNSLocation.Layer2)), provider));
        services.put(NamingServiceType.ZNS, new ZNS(serviceConfigs.get(NamingServiceType.ZNS), provider));
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
    private Network getNetworkId(String blockchainProviderUrl, Boolean shouldForwardError) {
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
