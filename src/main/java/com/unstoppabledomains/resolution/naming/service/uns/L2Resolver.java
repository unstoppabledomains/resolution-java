package com.unstoppabledomains.resolution.naming.service.uns;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NSExceptionParams;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;

/**
 * 
 */
public class L2Resolver {
  private ExecutorService executor = Executors.newFixedThreadPool(2);
  
  public <T> T resolveOnBothLayers(Callable<T> l1Func, Callable<T> l2Func) throws NamingServiceException{
    Future<T> l1result = executor.submit(l1Func);
    Future<T> l2result = executor.submit(l2Func);

    try {
      return processFutureResult(l2result);
    } catch (NamingServiceException e) {
      if (e.getCode() != NSExceptionCode.UnregisteredDomain && e.getCode() != NSExceptionCode.UnspecifiedResolver) {
        throw e;
      }
    }
    return processFutureResult(l1result);
  }

  private <T> T processFutureResult(Future<T> result) throws NamingServiceException {
    try {
      return result.get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof NamingServiceException) {
        NamingServiceException nsException = (NamingServiceException) e.getCause();
        throw nsException;
      } else {
        throw new NamingServiceException(NSExceptionCode.UnknownError, NSExceptionParams.EMPTY_PARAMS, e.getCause());
      }
    } catch (Exception e) {
      throw new NamingServiceException(NSExceptionCode.UnknownError, NSExceptionParams.EMPTY_PARAMS, e);
    }
  }
}