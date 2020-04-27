package com.unstoppabledomains.resolution;

import org.web3j.protocol.Web3j;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.ClientTransactionManager;

import org.web3j.ens.NameHash;

abstract class NamingService extends Utilities {
    public static Web3j web3;
    public static TransactionManager transactionManager;

    public NamingService(String name, Web3j web3, Boolean verbose) {
        super(name, verbose);
        NamingService.web3 = web3;
        NamingService.transactionManager = new ClientTransactionManager(web3, null);
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