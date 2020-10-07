package com.unstoppabledomains.resolution.contracts.cns;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.Contract;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProxyReader extends Contract {

    private static final String ABI_FILE = "src/main/resources/abi/proxy_reader_abi.json";

    public ProxyReader(String url, String address) {
        super(url, address);
    }

    @Override
    protected JsonArray getAbi() {
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(Paths.get(ABI_FILE)));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't find an ABI for " + getClass().getSimpleName() + " contract", e);
        }
        return new JsonParser().parse(jsonString).getAsJsonArray();
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
