package com.unstoppabledomains.resolution.naming.service;

import com.unstoppabledomains.exceptions.NamingServiceException;

public interface NamingService {
    Boolean isSupported(String domain);

    String getAddress(String domain, String ticker) throws NamingServiceException;

    String getIpfsHash(String domain) throws NamingServiceException;

    String getEmail(String domain) throws NamingServiceException;

    String getOwner(String domain) throws NamingServiceException;

    String getNamehash(String domain) throws NamingServiceException;
}