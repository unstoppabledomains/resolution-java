package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NamingServiceException;

abstract class NamingService {
    protected abstract Boolean isSupported(String domain);
    protected abstract String addr(String domain, String ticker) throws NamingServiceException;
    protected abstract String ipfsHash(String domain) throws NamingServiceException;
    protected abstract String email(String domain) throws NamingServiceException;
    protected abstract String owner(String domain) throws NamingServiceException;

    protected String namehash(String domain) {
        return NameHash.nameHash(domain);
    }
}