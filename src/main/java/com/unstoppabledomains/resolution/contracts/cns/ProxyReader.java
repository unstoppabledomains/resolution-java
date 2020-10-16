package com.unstoppabledomains.resolution.contracts.cns;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.unstoppabledomains.resolution.contracts.Contract;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.stream.Collectors;

public class ProxyReader extends Contract {

    private static final String ABI_FILE = "proxy_reader_abi.json";

    public ProxyReader(String url, String address) {
        super(url, address);
    }

    public String getOwner(BigInteger tokenID) {
        try {
            Object[] args = new Object[1];
            args[0] = tokenID;
            return fetchAddress("ownerOf", args);
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

    @Override
    protected JsonArray getAbi() {
        final InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(ABI_FILE));

        String jsonString = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));

        return new JsonParser().parse(jsonString).getAsJsonArray();
    }
}
