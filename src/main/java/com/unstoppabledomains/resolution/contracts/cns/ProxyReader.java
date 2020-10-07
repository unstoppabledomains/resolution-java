package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

import java.math.BigInteger;

public class ProxyReader extends Contract {

    private static final String ABI_FILE = "src/main/resources/abi/proxy_reader_abi.json";

    public ProxyReader(String url, String address) {
        super(url, address, ABI_FILE);
    }

    public String getOwner(BigInteger tokenID) throws Exception {
        try {
            Object[] args = new Object[1];
            args[0] = tokenID;
            return fetchAddress("ownerOf", args);
        } catch (IllegalArgumentException e) {
            // params will be added on level above;
            throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("", ""), e);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRecord(String recordKey, BigInteger tokenID) throws Exception {
        Object[] args = new Object[2];
        args[0] = recordKey;
        args[1] = tokenID;

        return fetchOne("get", args);
    }
}
