package com.unstoppabledomains.resolution.naming.service;

import java.util.List;

import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;

public interface NamingService {
    Boolean isSupported(String domain);

    String getRecord(String domain, String recordKey) throws NamingServiceException;

    // String getAddress(String domain, String ticker) throws NamingServiceException;

    // String getIpfsHash(String domain) throws NamingServiceException;

    // String getEmail(String domain) throws NamingServiceException;

    String getOwner(String domain) throws NamingServiceException;

    String getNamehash(String domain) throws NamingServiceException;

    List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException;
}