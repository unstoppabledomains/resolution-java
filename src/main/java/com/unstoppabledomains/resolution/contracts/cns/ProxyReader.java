package com.unstoppabledomains.resolution.contracts.cns;

import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.BaseContract;

import java.math.BigInteger;

public class ProxyReader extends BaseContract {

    private static final String ABI_FILE = "cns/proxy_reader_abi.json";
    private static final String namingServiceName = "CNS";

    public ProxyReader(String url, String address) {
        super(namingServiceName, url, address);
    }

    public String getOwner(BigInteger tokenID) {
        try {
            Object[] args = { tokenID };
            return fetchAddress("ownerOf", args);
        } catch (Exception e) {
            return null;
        }
    }

    public String getRecord(String recordKey, BigInteger tokenID) throws Exception {
        Object[] args = { recordKey, tokenID };
        return fetchOne("get", args);
    }

    public ProxyData getProxyData(String[] records, BigInteger tokenID) throws NamingServiceException {
        Object[] args = new Object[2];
        args[0] = records;
        args[1] = tokenID;
        return fetchData(args);
    }

    @Override
    protected String getAbiPath() {
      return ABI_FILE;
    }
}
