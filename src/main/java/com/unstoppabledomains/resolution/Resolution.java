package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;

public class Resolution {
   private NamingService[] services;
   private String providerUrl;

   /**
    * @author Jeyhun Tahirov 
    * @see http://github.com/johnnyjumper/
    * Resolution object
    * @param blockchainProviderUrl - url for the public etherium provider
    */
    public Resolution(String blockchainProviderUrl) {
        this.providerUrl = blockchainProviderUrl;
        this.services = this.buildServices(providerUrl);
    }

    /**
     * Resolves domain for a specific ticker address
     * @param domain - domain name such as "brad.crypto"
     * @param ticker - coin ticker such as ETH
     * @return address in hex-string format
     */
    public String addr(String domain, String ticker) throws NamingServiceException {
        NamingService service = this.findService(domain);
        return service.addr(domain, ticker);
    }

    /**
     * Produces a namehash for a specific domain
     * @param domain - domain name such as "brad.crypto"
     * @see https://docs.ens.domains/contract-api-reference/name-processing
     * @return namehash of a domain for a specific NamingService
     */
    public String namehash(String domain) throws NamingServiceException {
        NamingService service = this.findService(domain);
        return service.namehash(domain);
    }
    
    /**
     * Resolves domain for an ipfs hash
     * @param domain - domain name such as "brad.crypto"
     * @see https://docs.ipfs.io/concepts/what-is-ipfs/
     * @return ipfs hash used to redirect people to ipfs content
     */
    public String ipfsHash(String domain) throws NamingServiceException {
        NamingService service = this.findService(domain);
        return service.ipfsHash(domain);
    }

    /**
     * Resolves an email address from a domain
     * @param domain - domain name such as "brad.crypto"
     * @return email address 
     */
    public String email(String domain) throws NamingServiceException {
        NamingService service = this.findService(domain);
        return service.email(domain);
    }

    /**
     * Resolves owner address from a domain
     * @param domain - domain name such as "brad.crypto"
     * @return etherium address of a domain's owner
     */
    public String owner(String domain) throws NamingServiceException {
        NamingService service = this.findService(domain);
        return service.owner(domain);
    }
    
    private NamingService findService(String domain) throws NamingServiceException {
        for (NamingService service : this.services) {
            if (service.isSupported(domain)) return service;
        }
        throw new NamingServiceException(NSExceptionCode.UnsupportedDomain, new NSExceptionParams("d", domain));
    }

    private NamingService[] buildServices(String providerUrl) {
        NamingService[] services = new NamingService[1];
        services[0] = new CNS(providerUrl);
        return services;
    }
}
