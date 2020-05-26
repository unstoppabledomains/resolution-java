package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NamingServiceException;
import org.web3j.ens.NameHash;

abstract class NamingService extends Utilities {

    public NamingService(String name, Boolean verbose) {
        super(name, verbose);
    }
    abstract Boolean isSupported(String domain);
    abstract String addr(String domain, String ticker) throws NamingServiceException;
    abstract String ipfsHash(String domain) throws NamingServiceException;
    abstract String email(String domain) throws NamingServiceException;
    abstract String owner(String domain) throws NamingServiceException;

    public String namehash(String domain) {
        return NameHash.nameHash(domain);
    }
}