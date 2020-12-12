package com.unstoppabledomains.resolution.naming.service;

import java.util.List;

import com.unstoppabledomains.exceptions.DnsException;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.util.DnsRecord;
import com.unstoppabledomains.util.DnsRecordsType;

public interface NamingService {
    Boolean isSupported(String domain);

    String getAddress(String domain, String ticker) throws NamingServiceException;

    String getIpfsHash(String domain) throws NamingServiceException;

    String getEmail(String domain) throws NamingServiceException;

    String getOwner(String domain) throws NamingServiceException;

    String getNamehash(String domain) throws NamingServiceException;

    List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException;
}