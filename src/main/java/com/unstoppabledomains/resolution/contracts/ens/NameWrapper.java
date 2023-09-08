package com.unstoppabledomains.resolution.contracts.ens;

import java.math.BigInteger;

import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;

public class NameWrapper extends BaseContract {

    private static final String ABI_FILE = "ens/config/ens_namewrapper_abi.json";  
    private static final String namingServiceName = "ENS";

    public NameWrapper(String url, String address, IProvider provider) {
        super(namingServiceName, url, address, provider);
    }

    public Boolean isWrappedDomain (BigInteger tokenId) throws NamingServiceException {
        Object[] args = new Object[1];
        args[0] = tokenId;
        return fetchOne("isWrapped", args);
    }

    @Override
    protected String getAbiPath() {
        return ABI_FILE;
    }
}
