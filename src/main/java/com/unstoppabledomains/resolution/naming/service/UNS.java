package com.unstoppabledomains.resolution.naming.service;


import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.unstoppabledomains.config.network.model.Network;
import com.unstoppabledomains.exceptions.dns.DnsException;
import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.contracts.interfaces.IProvider;
import com.unstoppabledomains.resolution.dns.DnsRecord;
import com.unstoppabledomains.resolution.dns.DnsRecordsType;

public class UNS extends BaseNamingService {
  private ExecutorService executor = Executors.newFixedThreadPool(2);
  private UNSInternal unsl1;
  private UNSInternal unsl2;

  public UNS(UNSConfig config, IProvider provider) {
    super(config.getLayer1(), provider);
    unsl1 = new UNSInternal(config.getLayer1(), provider);
    unsl2 = new UNSInternal(config.getLayer2(), provider);
  }

  @Override
  public Network getNetwork() {
    return unsl1.chainId;
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
    return resolveOnBothLayers(
      () -> {
        return unsl1.getRecord(domain, recordKey);
      },
      () -> {
        return unsl2.getRecord(domain, recordKey);
      });
  }

  @Override
  public String getOwner(String domain) throws NamingServiceException {
    return resolveOnBothLayers(
      () -> {
        return unsl1.getOwner(domain);
      },
      () -> {
        return unsl2.getOwner(domain);
      });
  }

  @Override
  public Map<String, String> batchOwners(List<String> domain) throws NamingServiceException {
    return resolveOnBothLayers(
      () -> {
        return unsl1.batchOwners(domain);
      },
      () -> {
        return unsl2.batchOwners(domain);
      });
  }

  @Override
  public List<DnsRecord> getDns(String domain, List<DnsRecordsType> types) throws NamingServiceException, DnsException {
    return resolveOnBothLayers(
      () -> {
        return unsl1.getDns(domain, types);
      },
      () -> {
        return unsl2.getDns(domain, types);
      });
  }

  @Override
  public String getTokenUri(BigInteger tokenID) throws NamingServiceException {
    return resolveOnBothLayers(
      () -> {
        return unsl1.getTokenUri(tokenID);
      },
      () -> {
        return unsl2.getTokenUri(tokenID);
      });
  }

  @Override
  public String getDomainName(BigInteger tokenID) throws NamingServiceException {
    return resolveOnBothLayers(
      () -> {
        return unsl1.getDomainName(tokenID);
      },
      () -> {
        return unsl2.getDomainName(tokenID);
      });
  }

  private <T> T processFutureResult(Future<T> result) throws NamingServiceException {
    try {
      return result.get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof NamingServiceException) {
        NamingServiceException nsException = (NamingServiceException) e.getCause();
        switch (nsException.getCode()) {
          case UnregisteredDomain:
          case UnspecifiedResolver:
            return null;
          default:
            throw nsException;
        }
      }
    } catch (Exception e) {
      throw new NamingServiceException(NSExceptionCode.UnknownError);
    }
    return null;
  }

  private <T> T resolveOnBothLayers(Callable<T> l1Func, Callable<T> l2Func) throws NamingServiceException{
    Future<T> l1result = executor.submit(l1Func);
    Future<T> l2result = executor.submit(l2Func);

    T result = processFutureResult(l2result);
    if (result == null) {
      result = processFutureResult(l1result);
    }
    if (result == null) {
      throw new NamingServiceException(NSExceptionCode.UnregisteredDomain);
    }
    return result;
  }
}
