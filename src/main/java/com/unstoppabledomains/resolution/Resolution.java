package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;

public class Resolution {
   private NamingService[] services;
   private String providerUrl;

   /**
    * @author Jeyhun Tahirov 
    * see http://github.com/johnnyjumper/
    * Resolution object
    * @param blockchainProviderUrl - url for the public etherium provider
    */
    public Resolution(String blockchainProviderUrl) {
        this.providerUrl = blockchainProviderUrl;
        this.services = buildServices(providerUrl);
    }

    /**
     * Resolves domain for a specific ticker address
     * @param domain - domain name such as "brad.crypto"
     * @param ticker - coin ticker such as ETH
     * @throws NamingServiceException - if tld is not recognized or there record for such currency is not presented
     * @return address in hex-string format
     */
    public String getAddress(String domain, String ticker) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getAddress(domain, ticker);
    }

    /**
     * Produces a getNamehash for a specific domain
     * @param domain - domain name such as "brad.crypto"
     * see https://docs.ens.domains/contract-api-reference/name-processing
     * @throws NamingServiceException - if tld of the domain is not recognized
     * @return getNamehash of a domain for a specific NamingService
     */
    public String getNamehash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getNamehash(domain);
    }

    
    /**
     * Resolves domain for an ipfs hash
     * @param domain - domain name such as "brad.crypto"
     * see https://docs.ipfs.io/concepts/what-is-ipfs/
     * @throws NamingServiceException - if no record is present
     * @return ipfs hash used to redirect people to ipfs content
     */
    public String getIpfsHash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getIpfsHash(domain);
    }

    /**
     * Resolves an getEmail address from a domain
     * @param domain - domain name such as "brad.crypto"
     * @throws NamingServiceException - if no getEmail is present
     * @return getEmail address
     */
    public String getEmail(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getEmail(domain);
    }

    /**
     * Resolves getOwner address from a domain
     * @param domain - domain name such as "brad.crypto"
     * @throws NamingServiceException - if getOwner is not present
     * @return etherium address of a domain's getOwner
     */
    public String getOwner(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getOwner(domain);
    }
    

    /**
     * Resolves domain for a specific ticker address
     * @deprecated this method is deprecated since 1.6.0. 
     * <p> Use {@link Resolution#getAddress(String, String)} instead.
     * @param domain - domain name such as "brad.crypto"
     * @param ticker - coin ticker such as ETH
     * @throws NamingServiceException - if tld is not recognized or there record for such currency is not presented
     * @return address in hex-string format
     */
    @Deprecated
    public String addr(String domain, String ticker) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getAddress(domain, ticker);
    }

    /**
     * Produces a getNamehash for a specific domain
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link Resolution#getNamehash(String)} instead.
     * @param domain - domain name such as "brad.crypto"
     * see https://docs.ens.domains/contract-api-reference/name-processing
     * @throws NamingServiceException - if tld of the domain is not recognized
     * @return getNamehash of a domain for a specific NamingService
     */
    @Deprecated
    public String namehash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getNamehash(domain);
    }
    
    /**
     * Resolves domain for an ipfs hash
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link Resolution#getIpfsHash(String)} instead.
     * @param domain - domain name such as "brad.crypto"
     * see https://docs.ipfs.io/concepts/what-is-ipfs/
     * @throws NamingServiceException - if no record is present
     * @return ipfs hash used to redirect people to ipfs content
     */
    @Deprecated
    public String ipfsHash(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getIpfsHash(domain);
    }

    /**
     * Resolves an getEmail address from a domain
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link Resolution#getEmail(String)} instead.
     * @param domain - domain name such as "brad.crypto"
     * @throws NamingServiceException - if no getEmail is present
     * @return getEmail address
     */
    @Deprecated
    public String email(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getEmail(domain);
    }

    /**
     * Resolves getOwner address from a domain
     * @deprecated this method is deprecated since 1.6.0.
     * <p> Use {@link Resolution#getOwner(String)} instead.
     * @param domain - domain name such as "brad.crypto"
     * @throws NamingServiceException - if getOwner is not present
     * @return etherium address of a domain's getOwner
     */
    @Deprecated
    public String owner(String domain) throws NamingServiceException {
        NamingService service = findService(domain);
        return service.getOwner(domain);
    }
    
    private NamingService findService(String domain) throws NamingServiceException {
        for (NamingService service : services) {
            if (Boolean.TRUE.equals(service.isSupported(domain))) return service;
        }
        throw new NamingServiceException(NSExceptionCode.UnsupportedDomain, new NSExceptionParams("d", domain));
    }

    private NamingService[] buildServices(String providerUrl) {
        NamingService[] services = new NamingService[3];
        services[0] = new CNS(providerUrl);
        services[1] = new ENS(providerUrl);
        services[2] = new ZNS("https://api.zilliqa.com");
        return services;
    }
}
