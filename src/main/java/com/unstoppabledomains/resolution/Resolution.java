package com.unstoppabledomains.resolution;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.unstoppabledomains.config.network.NetworkConfigLoader;
import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.config.network.model.Location;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.DefaultProvider;
import com.unstoppabledomains.resolution.contracts.JsonProvider;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.naming.service.NSConfig;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;
import com.unstoppabledomains.resolution.naming.service.ZNS;
import com.unstoppabledomains.resolution.naming.service.uns.L2Resolver;
import com.unstoppabledomains.resolution.naming.service.uns.ResolutionMethods;
import com.unstoppabledomains.resolution.naming.service.uns.UNS;
import com.unstoppabledomains.resolution.naming.service.uns.UNSConfig;
import com.unstoppabledomains.resolution.naming.service.uns.UNSLocation;
import com.unstoppabledomains.util.Utilities;

public class Resolution implements DomainResolution {
    private Map<NamingServiceType, NamingService> services;

    /**
     * Use {@link ResolutionBuilder} methods to override default configs
     * or get Resolution object with default settings
     * by calling {@link ResolutionBuilder#build()}
     *
     * @return builder object
     */
    public static ResolutionBuilder builder() {
        return new ResolutionBuilder(new ResolutionBuilderConnector());
    }

    /**
     * Create resolution object with Unstoppable Domains' api key and default provider ZNS:
     * <a href="https://unstoppabledomains.com/partner-api-dashboard">Unstoppable Domains'</a> proxy blockchain provider for UNS
     * <a href="https://zilliqa.com">zilliqa</a> for ZNS
     */
    public Resolution(String apiKey) {
        IProvider provider = new DefaultProvider();
        services = getServices(provider, apiKey);
    }

    private Resolution(Map<NamingServiceType, NamingService> services) {
        this.services = services;
    }

    @Override
    public boolean isSupported(String domain) throws NamingServiceException {
        for (NamingService service: services.values()) {
            if (service.isSupported(domain)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Network getNetwork(NamingServiceType type) {
        NamingService service = services.get(type);
        return service.getNetwork();
    }

    @Override
    public Map<String, String> getAllRecords(String domain) throws NamingServiceException {
        String normailzedDomain = normalizeDomain(domain);
        return callServicesForDomain(normailzedDomain, (service) -> service.getAllRecords(normailzedDomain));
    }

    @Override
    public String getRecord(String domain, String recordKey) throws NamingServiceException {
        String normailzedDomain = normalizeDomain(domain);
        return callServicesForDomain(normailzedDomain, (service) -> service.getRecord(normailzedDomain, recordKey));
    }

    @Override
    public Map<String, String> getRecords(String domain, List<String> recordsKeys) throws NamingServiceException {
        String normailzedDomain = normalizeDomain(domain);
        return callServicesForDomain(normailzedDomain, (service) -> service.getRecords(normailzedDomain, recordsKeys));
    }

    @Override
    public String getAddress(String domain, String ticker) throws NamingServiceException {
        String recordKey = "crypto." + ticker.toUpperCase() + ".address";
        try {
            return getRecord(domain, recordKey);
        } catch(NamingServiceException exception) {
            if (exception.getCode() == NSExceptionCode.RecordNotFound) {
                throw new NamingServiceException(NSExceptionCode.UnknownCurrency, 
                    new NSExceptionParams("d|c", domain, ticker));
            }
            throw exception;
        }
    }

    @Override
    public String getMultiChainAddress(String domain, String ticker, String chain) throws NamingServiceException {
        String recordKey = "crypto." + ticker.toUpperCase() + ".version." + chain.toUpperCase() + ".address";
        return getRecord(domain, recordKey);
    }

    @Override
    public String getNamehash(String domain, NamingServiceType serviceType) throws NamingServiceException {
        domain = normalizeDomain(domain);
        switch (serviceType) {
            case ZNS:
                return services.get(NamingServiceType.ZNS).getNamehash(domain);
            default:
                return services.get(NamingServiceType.UNS).getNamehash(domain);
        }
    }

    @Override
    public String getIpfsHash(String domain) throws NamingServiceException {
        String recordKey = "dweb.ipfs.hash";
        return getRecord(domain, recordKey);
    }

    @Override
    public String getEmail(String domain) throws NamingServiceException {
        String recordKey = "whois.email.value";
        return getRecord(domain, recordKey);
    }

    @Override
    public String getOwner(String domain) throws NamingServiceException {
        String normailzedDomain = normalizeDomain(domain);
        return callServicesForDomain(normailzedDomain, (service) -> service.getOwner(normailzedDomain));
    }

    @Override
    public Map<String, String> getBatchOwners(List<String> domains) throws NamingServiceException {
        NamingService zns = services.get(NamingServiceType.ZNS);
        NamingService uns = services.get(NamingServiceType.UNS);

        Map<String, String> unsOwners = uns.batchOwners(domains);

        List<String> znsDomains = domains.stream().filter(d -> {
            try {
                return zns.isSupported(d) && unsOwners.get(d) == null;
            } catch (NamingServiceException e) {
                return false;
            }
        }).collect(Collectors.toList());
        
        Map<String, String> znsOwners = zns.batchOwners(znsDomains);

        znsOwners.forEach((k, v) -> {
            if (v != null) {
                unsOwners.merge(k, v, (v1, v2) -> v2);
            }
        });
        return unsOwners;
    }

    @Override
    public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException {
        String normailzedDomain = normalizeDomain(domain);
        return callServicesForDomain(normailzedDomain, (service) -> service.getDns(normailzedDomain, types));
    }

    @Override
    public String getTokenURI(String domain) throws NamingServiceException {
        String normalizedDomain = normalizeDomain(domain);
        try {
            return callServicesForDomain(normalizedDomain, (service) -> {
                String namehash = service.getNamehash(normalizedDomain);
                BigInteger tokenId = Utilities.namehashToTokenID(namehash);
                return service.getTokenUri(tokenId);
            });
        } catch (NamingServiceException e) {
            if (e.getCode() == NSExceptionCode.UnregisteredDomain) {
                throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d|m", domain, "tokenURI"), e);
            }
            throw e;
        }
    }

    @Override
    public TokenUriMetadata getTokenURIMetadata(String domain) throws NamingServiceException {
        String tokenURI = getTokenURI(domain);
        return getMetadataFromTokenURI(tokenURI);
    }

    @Override
    public String unhash(String hash, NamingServiceType serviceType) throws NamingServiceException {
        NamingService service = services.get(serviceType);
        BigInteger tokenId = Utilities.namehashToTokenID(hash);
        String domainName = service.getDomainName(tokenId);
        if (!service.getNamehash(domainName).equals(hash)) {
            throw new NamingServiceException(NSExceptionCode.UnknownError, new NSExceptionParams("m", "unhash"));
        }
        return domainName;
    }

    @Override
    public Map<String, Location> getLocations(String... domains) throws NamingServiceException {
        NamingService uns = services.get(NamingServiceType.UNS);
        Map<String, Location> unsLocations = uns.getLocations(domains);

        return loadZnsLocations(domains, unsLocations);
    }

    @Override
    public String getReverseTokenId(String address) throws NamingServiceException {
        if (!Utilities.verifyAddress(address)) {
            throw new NamingServiceException(NSExceptionCode.IncorrectAddress);
        }
        NamingService service = services.get(NamingServiceType.UNS); // reverse is supported only for UNS 
        return service.getReverseTokenId(address);
    }

    @Override
    public String getReverseTokenId(String address, UNSLocation location) throws NamingServiceException {
        if (!Utilities.verifyAddress(address)) {
            throw new NamingServiceException(NSExceptionCode.IncorrectAddress);
        }
        UNS service = (UNS) services.get(NamingServiceType.UNS); // reverse is supported only for UNS 
        return service.getReverseTokenId(address, location);
    }

    @Override
    public String getReverse(String address) throws NamingServiceException {
        if (!Utilities.verifyAddress(address)) {
            throw new NamingServiceException(NSExceptionCode.IncorrectAddress);
        }
        NamingService service = services.get(NamingServiceType.UNS); // reverse is supported only for UNS 
        String tokenIdHash = service.getReverseTokenId(address);
        return unhash(tokenIdHash, NamingServiceType.UNS);
    }

    @Override
    public String getReverse(String address, UNSLocation location) throws NamingServiceException {
        if (!Utilities.verifyAddress(address)) {
            throw new NamingServiceException(NSExceptionCode.IncorrectAddress);
        }
        UNS service = (UNS) services.get(NamingServiceType.UNS); // reverse is supported only for UNS 
        String tokenIdHash = service.getReverseTokenId(address, location);
        return unhash(tokenIdHash, NamingServiceType.UNS);
    }

    @Override
    public String getAddress(String domain, String network, String token) throws NamingServiceException {
        String normailzedDomain = normalizeDomain(domain);
        UNS service = (UNS) services.get(NamingServiceType.UNS); // getAddress is supported only for UNS
        String address = service.getAddress(normailzedDomain, network, token);
        return address;
    }

    private TokenUriMetadata getMetadataFromTokenURI(String tokenURI) throws NamingServiceException {
        try {
            JsonProvider provider = new JsonProvider();
            return provider.request(tokenURI, TokenUriMetadata.class);
        } catch (Exception e) {
            throw new NamingServiceException(NSExceptionCode.UnknownError, new NSExceptionParams("m", "getMetadataFromTokenURI"), e);
        }
    }

    private Map<NamingServiceType, NamingService> getServices(IProvider provider, String apiKey) {
        provider.setHeader("Authorization", "Bearer " + apiKey);
        provider.setHeader("X-Lib-Agent", DefaultProvider.getUserAgent());

        String unsProxyAddress = NetworkConfigLoader.getContractAddress(Network.MAINNET, "ProxyReader");
        String unsl2ProxyAddress = NetworkConfigLoader.getContractAddress(Network.MATIC_MAINNET, "ProxyReader");
        Map<NamingServiceType, NamingService> namingServices = new HashMap<>();
        namingServices.put(NamingServiceType.UNS, new UNS(new UNSConfig(
                new NSConfig(Network.MAINNET, ResolutionBuilder.UD_RPC_PROXY_BASE_URL + "/chains/eth/rpc", unsProxyAddress),
                new NSConfig(Network.MATIC_MAINNET, ResolutionBuilder.UD_RPC_PROXY_BASE_URL + "/chains/matic/rpc", unsl2ProxyAddress))
                , provider));
        namingServices.put(NamingServiceType.ZNS, new ZNS(new NSConfig(Network.MAINNET, ResolutionBuilder.ZILLIQA_DEFAULT_URL, ResolutionBuilder.ZNS_DEFAULT_REGISTRY_ADDRESS), provider));
        
        return namingServices;
    }

    private interface ThrowFunc<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    private <T> T callServicesForDomain(String domain, ThrowFunc<NamingService, T, Exception> func) throws NamingServiceException{
        NamingService zns = services.get(NamingServiceType.ZNS);
        NamingService uns = services.get(NamingServiceType.UNS);

        L2Resolver resolver = new L2Resolver();
        return resolver.resolve(ResolutionMethods.<T>builder()
        .l2Func(() -> {
            if (!zns.isSupported(domain)) {
                throw new NamingServiceException(NSExceptionCode.UnsupportedDomain, new NSExceptionParams("d", domain));
            }
            return func.apply(zns);
        })
        .l1Func(() -> {
            return func.apply(uns);
        }).build());
    }

    private String normalizeDomain(String domain) throws NamingServiceException {
        String normalizedDomain = domain.trim().toLowerCase();
        if (!normalizedDomain.matches("^[.a-z\\d-]+$")) {
            throw new NamingServiceException(NSExceptionCode.InvalidDomain, new NSExceptionParams("d", domain));
        }
        return normalizedDomain;
    }

    private Map<String, Location> loadZnsLocations(String[] domains, Map<String, Location> unsLocations) throws NamingServiceException {
        NamingService zns = services.get(NamingServiceType.ZNS);

        String[] znsDomains = Arrays.stream(domains).filter(d -> {
            try {
                return zns.isSupported(d) && unsLocations.get(d) == null;
            } catch (NamingServiceException e) {
                return false;
            }
        }).toArray(String[]::new);

        Map<String, Location> znsLocations = zns.getLocations(znsDomains);

        znsLocations.forEach((k, v) -> {
            if (v != null) {
                unsLocations.merge(k, v, (v1, v2) -> v2);
            }
        });

        return unsLocations;
    }

    // Allows to create a class instance with a private constructor
    public static final class ResolutionBuilderConnector {
        private ResolutionBuilderConnector() {}

        public Resolution buildResolution(Map<NamingServiceType, NamingService> services) {
            return new Resolution(services);
        }
    }
}
