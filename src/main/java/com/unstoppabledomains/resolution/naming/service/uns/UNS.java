package com.unstoppabledomains.resolution.naming.service.uns;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;
import com.unstoppabledomains.resolution.naming.service.NamingService;
import com.unstoppabledomains.resolution.naming.service.NamingServiceType;

public class UNS implements NamingService {
  private L2Resolver resolver;
  private UNSInternal unsl1;
  private UNSInternal unsl2;

  public UNS(UNSConfig config, IProvider provider) {
    resolver = new L2Resolver();
    unsl1 = new UNSInternal(UNSLocation.Layer1, config.getLayer1(), provider);
    unsl2 = new UNSInternal(UNSLocation.Layer2, config.getLayer2(), provider);
  }

  @Override
  public Network getNetwork() {
    return unsl1.getNetwork();
  }

  @Override
  public NamingServiceType getType() {
    return NamingServiceType.UNS;
  }

  @Override
  public Boolean isSupported(String domain) throws NamingServiceException {
    return unsl1.isSupported(domain);
  }

  @Override
  public String getNamehash(String domain) throws NamingServiceException {
    return unsl1.getNamehash(domain);
  }

  @Override
  public String getRecord(String domain, String recordKey) throws NamingServiceException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.getRecord(domain, recordKey);
      },
      () -> {
        return unsl2.getRecord(domain, recordKey);
      });
  }

  @Override
  public String getOwner(String domain) throws NamingServiceException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.getOwner(domain);
      },
      () -> {
        return unsl2.getOwner(domain);
      });
  }

  @Override
  public Map<String, String> batchOwners(List<String> domain) throws NamingServiceException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.batchOwners(domain);
      },
      () -> {
        return unsl2.batchOwners(domain);
      });
  }

  @Override
  public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.getDns(domain, types);
      },
      () -> {
        return unsl2.getDns(domain, types);
      });
  }

  @Override
  public String getTokenUri(BigInteger tokenID) throws NamingServiceException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.getTokenUri(tokenID);
      },
      () -> {
        return unsl2.getTokenUri(tokenID);
      });
  }

  @Override
  public String getDomainName(BigInteger tokenID) throws NamingServiceException {
    return resolver.resolveOnBothLayers(
      () -> {
        return unsl1.getDomainName(tokenID);
      },
      () -> {
        return unsl2.getDomainName(tokenID);
      });
  }
}
