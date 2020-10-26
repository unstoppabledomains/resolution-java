package com.unstoppabledomains.resolution;

import com.unstoppabledomains.exceptions.NamingServiceException;

abstract interface NamingService {
    Boolean isSupported(String domain);
    String addr(String domain, String ticker) throws NamingServiceException;
    String ipfsHash(String domain) throws NamingServiceException;
    String email(String domain) throws NamingServiceException;
    String owner(String domain) throws NamingServiceException;
    String namehash(String domain) throws NamingServiceException;
}