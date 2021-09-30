package com.unstoppabledomains.resolution.contracts.uns;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.esaulpaugh.headlong.abi.Tuple;
import com.unstoppabledomains.config.network.model.Location;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.BaseContract;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.util.Utilities;

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

    public List<String> batchOwners(BigInteger[] tokenIDs) throws Exception {
        Object[] args = { tokenIDs };
        BigInteger[] owners = fetchOne("ownerOfForMany", args);
        List<String> convertedOwners = new ArrayList<>();
        for (BigInteger owner: owners) {
            String hexRepresentation = owner.toString(16);
            convertedOwners.add("0x" + hexRepresentation);
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

    public List<Location.LocationBuilder> getLocationAddresses(BigInteger... tokenIDs) throws Exception {
        List<MulticallArgs> args = new ArrayList<>();
        args.add(new MulticallArgs("getDataForMany", new Object[]{new String[]{}, tokenIDs}));
        for (BigInteger tokenId : tokenIDs) {
            args.add(new MulticallArgs("registryOf", new Object[]{tokenId}));
        }

        List<Tuple> results = fetchMulticall(args);

        BigInteger[] resolvers = (BigInteger[])results.get(0).get(0);
        BigInteger[] owners = (BigInteger[])results.get(0).get(1);
        List<Location.LocationBuilder> locations = new ArrayList<>();
        for (int i = 0; i < tokenIDs.length; i++) {
            if (!owners[i].equals(BigInteger.ZERO)) {
                Location.LocationBuilder location = Location.builder();
                location.RegistryAddress(Utilities.convertEthAddress((BigInteger)results.get(i + 1).get(0)));
                location.ResolverAddress(Utilities.convertEthAddress(resolvers[i]));
                location.Owner(Utilities.convertEthAddress(owners[i]));
                locations.add(location);
            } else {
                locations.add(null);
            }
        }
        return locations;
    }

    @Override
    protected String getAbiPath() {
      return ABI_FILE;
    }
}
