package com.unstoppabledomains.resolution.contracts.uns;

import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.util.Utilities;
import com.unstoppabledomains.resolution.artifacts.Numeric;

import java.math.BigInteger;

public class ProxyReader extends BaseContract {

    private static final String ABI_FILE = "uns/proxy_reader_abi.json";
    private static final String namingServiceName = "UNS";

    public ProxyReader(String url, String address, IProvider provider) {
        super(namingServiceName, url, address, provider);
    }

    public String getOwner(BigInteger tokenID) {
        try {
            Object[] args = { tokenID };
            return fetchAddress("ownerOf", args);
        } catch (Exception e) {
            return null;
        }
    }

    public String[] batchOwners(BigInteger[] tokenIDs) throws Exception {
            Object[] args = { tokenIDs };
            BigInteger[] owners = fetchOne("ownerOfForMany", args);
            String[] convertedOwners = new String[owners.length];
            for (int i =0; i < owners.length; i++) {
                String hexRepresentation = owners[i].toString(16);
                convertedOwners[i] = "0x" + hexRepresentation;
            }
            return convertedOwners;
    }

    public String getRecord(String recordKey, BigInteger tokenID) throws Exception {
        Object[] args = { recordKey, tokenID };
        return fetchOne("get", args);
    }

    public ProxyData getProxyData(String[] records, BigInteger tokenID) throws NamingServiceException {
        Object[] args = { records, tokenID };
        return fetchData(args);
    }

    public String getTokenUri(BigInteger tokenID) throws Exception {
        Object[] args = { tokenID };
        return fetchOne("tokenURI", args);
    }

    public Boolean getExists(BigInteger tokenID) throws NamingServiceException {
        Object[] args = { tokenID };
        return fetchOne("exists", args);
    }

    public String registryOf(BigInteger tokenID) {
        try {
            Object[] args = { tokenID };
            return fetchAddress("registryOf", args);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getAbiPath() {
      return ABI_FILE;
    }
}
