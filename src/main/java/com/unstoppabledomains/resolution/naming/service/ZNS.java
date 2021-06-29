package com.unstoppabledomains.resolution.naming.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.HTTPUtil;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.util.Utilities;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.math.BigInteger;

public class ZNS extends BaseNamingService {
    static final String REGISTRY_ADDRESS = "0x9611c53BE6d1b32058b2747bdeCECed7e1216793"; // eth style zil registry
                                                                                         // address
    static final String RECORDS_KEY = "records";

    public ZNS(NSConfig nsConfig, IProvider provider) {
        super(nsConfig, provider);
    }

    @Override
    public String getNamehash(String domain) {
        // Attaching parent value to the end of the domain to make sure it is a first
        // element in a resulting array
        domain = domain + ".0000000000000000000000000000000000000000000000000000000000000000";
        String[] labels = domain.split("\\.");

        Collections.reverse(Arrays.asList(labels));
        Optional<String> namehash = Arrays.stream(labels)
                .reduce((parent, label) -> Utilities.sha256(parent + Utilities.sha256(label), true));
        return "0x" + namehash.get();
    }

    @Override
    public NamingServiceType getType() {
      return NamingServiceType.ZNS;
    }

    @Override
    public Boolean isSupported(String domain) throws NamingServiceException {
        if (domain.equals("zil")) {
            return true;
        }
        return StringUtils.endsWith(domain, ".zil");
    }

    @Override
    public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException {
        throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m|n", "getDns", "ZNS"));
    }

    @Override
    public String getOwner(String domain) throws NamingServiceException {
        String[] addresses = getRecordAddresses(domain);
        if (addresses == null || Utilities.isEmptyResponse(addresses[0])) {
            throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
        }
        return addresses[0];
    }

    @Override
    public String getRecord(String domain, String key) throws NamingServiceException {
        try {
            JsonObject records = getAllRecords(domain);
            if (key.equals("dweb.ipfs.hash") || key.equals("ipfs.html.value")) {
                return getIpfsHash(records);
            }
            return records.get(key).getAsString();
        } catch(NullPointerException exception) {
            throw new NamingServiceException(NSExceptionCode.RecordNotFound, new NSExceptionParams("d|r", domain, key));
        }
    }

    @Override
    public String getTokenUri(BigInteger tokenID) throws NamingServiceException {
        throw new NamingServiceException(NSExceptionCode.NotImplemented, new NSExceptionParams("m|n", "getTokenUri", "ZNS"));
    }

    private String getIpfsHash(JsonObject records) {
        JsonElement newRecord = records.get("dweb.ipfs.hash");
        JsonElement oldRecord = records.get("ipfs.html.value");
        if (newRecord == null) {
            if (oldRecord == null) {
                throw new NullPointerException();
            }
            return oldRecord.getAsString();
        }
        return newRecord.getAsString();
    }

    private JsonObject getAllRecords(String domain) throws NamingServiceException {
        try {
            String resolverAddress = getResolverAddress(domain);
            String[] keys = {};
            JsonObject response = fetchSubState(resolverAddress, RECORDS_KEY, keys);
            return response.getAsJsonObject(RECORDS_KEY);
        } catch (IOException error) {
            throw new NamingServiceException(NSExceptionCode.RecordNotFound);
        }
    }

    private String getResolverAddress(String domain) throws NamingServiceException {
        String[] addresses = getRecordAddresses(domain);
        if (addresses == null || Utilities.isEmptyResponse(addresses[0])) {
            throw new NamingServiceException(NSExceptionCode.UnregisteredDomain, new NSExceptionParams("d", domain));
        }
        if (Utilities.isEmptyResponse(addresses[1])) {
            throw new NamingServiceException(NSExceptionCode.UnspecifiedResolver, new NSExceptionParams("d", domain));
        }
        return addresses[1];
    }

    private String[] getRecordAddresses(String domain) throws NamingServiceException {
        String namehash = getNamehash(domain);
        String[] keys = { namehash };
        try {
          JsonObject substate = fetchSubState(REGISTRY_ADDRESS, RECORDS_KEY, keys);
          JsonObject records = substate.getAsJsonObject(RECORDS_KEY);
          JsonObject domainSpecific = records.getAsJsonObject(namehash);
          JsonArray arguments = domainSpecific.getAsJsonArray("arguments");
          List<String> list = new ArrayList<>();
          for (JsonElement argument: arguments) {
            list.add(argument.getAsString());
          }
          return list.toArray(new String[list.size()]);
        } catch (IOException error) {
            throw new NamingServiceException(NSExceptionCode.BlockchainIsDown, new NSExceptionParams("n", "ZNS"), error);
        } catch (IllegalStateException exception) {
            return null;
        }
    }

    private JsonObject fetchSubState(String address, String field, String[] keys) throws IOException {
        JsonArray params = new JsonArray();
        params.add(address.replace("0x", ""));
        params.add(field);

        JsonArray keysJson = new JsonArray();
        for (String key: keys) { keysJson.add(key); }
        params.add(keysJson);

        String method = "GetSmartContractSubState";
        JsonObject body = HTTPUtil.prepareBody(method, params);
        JsonObject response = provider.request(blockchainProviderUrl, body);
        JsonElement result = response.get("result");
        return result.getAsJsonObject();
    }
}
