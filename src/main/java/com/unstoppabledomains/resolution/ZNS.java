package com.unstoppabledomains.resolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Numeric;
import com.unstoppabledomains.resolution.contracts.HTTPUtil;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class ZNS extends NamingService {
    static final String REGISTRY_ADDRESS = "0x9611c53BE6d1b32058b2747bdeCECed7e1216793"; // eth style zil registry
                                                                                         // address
    private final String provider;

    public ZNS(String blockchainProviderUrl) {
        this.provider = blockchainProviderUrl;
    }

    @Override
    public String namehash(String domain) {
        // Attaching parent value to the end of the domain to make sure it is a first
        // element in a resulting array
        domain = domain + ".0000000000000000000000000000000000000000000000000000000000000000";
        String[] labels = domain.split("\\.");

        Collections.reverse(Arrays.asList(labels));
        Optional<String> namehash = Arrays.stream(labels)
                .reduce((parent, label) -> sha256(parent + sha256(label), true));
        return "0x" + namehash.get();
    }

    public Boolean isSupported(String domain) {
        String[] split = domain.split("\\.");
        return (split.length != 0 && split[split.length - 1].equals("zil"));
    }

    public String addr(String domain, String ticker) throws NamingServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public String ipfsHash(String domain) throws NamingServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public String email(String domain) throws NamingServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public String owner(String domain) throws NamingServiceException {
        String[] addresses = getRecordAddresses(domain);
        if (Utilities.isNull(addresses[0])) {
            throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("%d", domain));
        }
        return addresses[0];
    }

    private String getResolverAddress(String domain) throws NamingServiceException {
        String[] addresses = getRecordAddresses(domain);
        if (Utilities.isNull(addresses[0])) {
            throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("%d", domain)); 
        }
        if (Utilities.isNull(addresses[1])) {
            throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver, new NSExceptionParams("%d", domain));
        }
        return addresses[1];
    }

    private String[] getRecordAddresses(String domain) throws NamingServiceException {
        String namehash = namehash(domain);
        String[] keys = { namehash };
        try {
          JsonObject substate = (JsonObject) fetchSubState(REGISTRY_ADDRESS, "records", keys);
          JsonObject records = (JsonObject) substate.get("records");
          JsonObject domainSpecific = (JsonObject) records.get(namehash);
          JsonArray arguments = domainSpecific.getAsJsonArray("arguments");
          List<String> list = new ArrayList<>();
          for (JsonElement argument: arguments) {
            list.add(argument.getAsString());
          }
          return list.toArray(new String[list.size()]);
        } catch (IOException error) {
            throw new NamingServiceException(NSExceptionCode.BlockchainIsDown, new NSExceptionParams("%n", "ZNS"), error);
        }
    }

    private JsonElement fetchSubState(String address, String field, String[] keys) throws IOException {
        JsonArray params = new JsonArray();
        params.add(address.replace("0x", ""));
        params.add(field);
        JsonArray keysJson = new JsonArray();
        for (String key: keys) {
            keysJson.add(key);
        }
        params.add(keysJson);
        String method = "GetSmartContractSubState";
        JsonObject body = HTTPUtil.prepareBody(method, params);
        JsonObject response = HTTPUtil.post(this.provider, body);
        return response.get("result");
    }

    private static String sha256(String key) {
        return sha256(key, false);
    }

    private static String sha256(String key, boolean hexEncoding) {
        SHA256Digest digester = new SHA256Digest();
        byte[] retValue = new byte[digester.getDigestSize()];
        if (hexEncoding) {
            digester.update(Numeric.hexStringToByteArray(key), 0, Numeric.hexStringToByteArray(key).length);
        } else {
            digester.update(key.getBytes(), 0, key.length());
        }
        digester.doFinal(retValue, 0);
        String result = Numeric.toHexString(retValue);
        return result.replace("0x", "");
    }
}
