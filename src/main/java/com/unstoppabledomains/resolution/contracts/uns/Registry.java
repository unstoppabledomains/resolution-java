package com.unstoppabledomains.resolution.contracts.uns;

import com.esaulpaugh.headlong.abi.Tuple;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.ContractLogs;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.util.Utilities;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Registry extends BaseContract {
    private static final String ABI_FILE = "uns/registry_abi.json";
    private static final String NAMING_SERVICE_NAME = "UNS";

    public Registry(String url, String address, IProvider provider) {
        super(NAMING_SERVICE_NAME, url, address, provider);

    }

    public String getDomainName(BigInteger tokenID) {
        try {
            String[] args = { Utilities.tokenIDToNamehash(tokenID) };
            List<Tuple> logs = fetchLogs("earliest", "NewURI", args);
            if (logs.size() == 0 || logs.get(0).size() == 0) {
                return null;
            }
            Tuple log = logs.get(0);
            return (String) log.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<String> getTokensOwnedBy(String address, String since) throws NamingServiceException {
        String[] args = { null, Utilities.normalizeAddress(address) };
        List<ContractLogs> logs = fetchContractLogs(since, "Transfer", args);
        return logs.stream()
            .map(log -> Utilities.normalizeAddress(log.getTopics().get(3)))
            .map(topic -> getNewUri(topic, since))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());            
    }

    @Override
    protected String getAbiPath() {
      return ABI_FILE;
    }

    private String getNewUri(String topic, String since) {
        try {
            String[] newUriArgs = { topic };
            return fetchLogs(since, "NewURI", newUriArgs)
                .get(0)
                .toString()
                .replaceAll("[\\[\\]]", "");
        } catch (NamingServiceException e) {
            return null;
        }  
    }
}
