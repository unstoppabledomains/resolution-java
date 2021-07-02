package com.unstoppabledomains.resolution.contracts.uns;

import com.esaulpaugh.headlong.abi.Tuple;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.util.Utilities;

import java.math.BigInteger;
import java.util.List;

public class Registry extends BaseContract {

    private static final String ABI_FILE = "cns/registry_abi.json";
    private static final String namingServiceName = "CNS";

    public Registry(String url, String address, IProvider provider) {
        super(namingServiceName, url, address, provider);
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

    @Override
    protected String getAbiPath() {
      return ABI_FILE;
    }
}
