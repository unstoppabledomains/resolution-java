package com.unstoppabledomains.resolution;

import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.naming.service.CNS;
import com.unstoppabledomains.resolution.naming.service.ENS;
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.ZNS;

import java.util.HashMap;
import java.util.Map;

public class Resolution implements DomainResolution {
    private static final String LINKPOOL_DEFAULT_URL = "https://main-rpc.linkpool.io";
    private static final String ZILLIQA_DEFAULT_URL = "https://api.zilliqa.com";

    private Map<NamingServiceType, NamingService> services;

    /**
     * Use {@link Builder} methods to override default configs
     * or get Resolution object with default settings
     * by calling {@link Builder#build()}
     *
     * @return builder object
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create resolution object with default config:
     * <a href="https://linkpool.io">linkpool</a> blockchain provider for ENS and CNS and
     * <a href="https://zilliqa.com">zilliqa</a> for ZNS
     */
    public Resolution() {
        services = getServices(LINKPOOL_DEFAULT_URL);
    }

    /**
     * Resolution object
     *
     * @param blockchainProviderUrl url for the public ethereum provider
     * @deprecated since 1.8.0
     * <p> Use {@link Resolution#builder()} instead
     */
    @Deprecated
    public Resolution(String blockchainProviderUrl) {
        services = getServices(blockchainProviderUrl);
    }

    private Resolution(Map<NamingServiceType, NamingService> services) {
        this.services = services;
    }

    @Override
    public String getAddress(String domain, String ticker) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getAddress(domain, ticker);
    }

    @Override
    public String getNamehash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getNamehash(domain);
    }

    @Override
    public String getIpfsHash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getIpfsHash(domain);
    }

    @Override
    public String getEmail(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getEmail(domain);
    }

    @Override
    public String getOwner(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getOwner(domain);
    }

    @Override
    public String addr(String domain, String ticker) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getAddress(domain, ticker);
    }

    @Override
    public String namehash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getNamehash(domain);
    }

    @Override
    public String ipfsHash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getIpfsHash(domain);
    }

    @Override
    public String email(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getEmail(domain);
    }

    @Override
    public String owner(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getOwner(domain);
    }

    private Map<NamingServiceType, NamingService> getServices(String blockchainProviderUrl) {
        return new HashMap<NamingServiceType, NamingService>() {{
            put(NamingServiceType.CNS, new CNS(new NSConfig(Network.MAINNET, blockchainProviderUrl)));
            put(NamingServiceType.ENS, new ENS(new NSConfig(Network.MAINNET, blockchainProviderUrl)));
            put(NamingServiceType.ZNS, new ZNS(new NSConfig(Network.MAINNET, ZILLIQA_DEFAULT_URL)));
        }};
    }

    private NamingService findService(String domain) throws NamingServiceException {
        for (NamingService service : services.values()) {
            if (Boolean.TRUE.equals(service.isSupported(domain))) return service;
        }
        throw new NamingServiceException(NSExceptionCode.UnsupportedDomain, new NSExceptionParams("d", domain));
    }

    public static class Builder {
        private static final String INFURA_URL = "https://%s.infura.io/v3/%s";

        private final Map<NamingServiceType, NSConfig> serviceConfigs;

        private Builder() {
            serviceConfigs = new HashMap<NamingServiceType, NSConfig>() {{
                put(NamingServiceType.CNS, new NSConfig(Network.MAINNET, LINKPOOL_DEFAULT_URL));
                put(NamingServiceType.ENS, new NSConfig(Network.MAINNET, LINKPOOL_DEFAULT_URL));
                put(NamingServiceType.ZNS, new NSConfig(Network.MAINNET, ZILLIQA_DEFAULT_URL));
            }};
        }

        /**
         * @param nsType  the naming service for which config is applied
         * @param chainId blockchain network ID
         * @return builder object to allow chaining
         */
        public Builder chainId(NamingServiceType nsType, Network chainId) {
            NSConfig nsConfig = serviceConfigs.get(nsType);
            nsConfig.setChainId(chainId);
            return this;
        }

        /**
         * @param nsType      the naming service for which config is applied
         * @param providerUrl blockchain provider URL
         * @return builder object to allow chaining
         */
        public Builder providerUrl(NamingServiceType nsType, String providerUrl) {
            NSConfig nsConfig = serviceConfigs.get(nsType);
            nsConfig.setBlockchainProviderUrl(providerUrl);
            return this;
        }

        /**
         * Configuration for <a href="https://infura.io">infura.io</a>
         * blockchain provider with previously set network ID using
         * {@link Builder#chainId(NamingServiceType, Network)} or default one
         * ({@link Network#MAINNET})
         *
         * @param nsType    the naming service for which config is applied
         * @param projectId Infura project ID
         * @return builder object to allow chaining
         */
        public Builder infura(NamingServiceType nsType, String projectId) {
            NSConfig nsConfig = serviceConfigs.get(nsType);
            final Network network = nsConfig.getChainId();
            return infura(nsType, network, projectId);
        }

        /**
         * Configuration for <a href="https://infura.io">infura</a> blockchain provider
         *
         * @param nsType    the naming service for which config is applied
         * @param chainId   blockchain network ID
         * @param projectId Infura project ID
         * @return builder object to allow chaining
         */
        public Builder infura(NamingServiceType nsType, Network chainId, String projectId) {
            NSConfig nsConfig = serviceConfigs.get(nsType);
            nsConfig.setChainId(chainId);

            String infuraUrl = String.format(INFURA_URL, chainId.getName(), projectId);
            nsConfig.setBlockchainProviderUrl(infuraUrl);

            return this;
        }

        /**
         * Call directly to get default configs or override them with {@link Builder} methods
         *
         * @return resolution object
         */
        public Resolution build() {
            Map<NamingServiceType, NamingService> services = new HashMap<NamingServiceType, NamingService>() {{
                put(NamingServiceType.CNS, new CNS(serviceConfigs.get(NamingServiceType.CNS)));
                put(NamingServiceType.ENS, new ENS(serviceConfigs.get(NamingServiceType.ENS)));
                put(NamingServiceType.ZNS, new ZNS(serviceConfigs.get(NamingServiceType.ZNS)));
            }};
            return new Resolution(services);
        }
    }
}
