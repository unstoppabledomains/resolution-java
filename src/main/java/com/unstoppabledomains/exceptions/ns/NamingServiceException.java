package com.unstoppabledomains.exceptions.ns;

public class NamingServiceException extends Exception {
  private static final long serialVersionUID = 1L;
  private final NSExceptionCode code;
  
  public NamingServiceException(NSExceptionCode code) {
    super(messageFromCode(code, new NSExceptionParams("", "")));
    this.code = code;
  }

  public NamingServiceException(NSExceptionCode code, NSExceptionParams params, Throwable cause) {
    super(messageFromCode(code, params), cause);
    this.code = code;
  }
  public NamingServiceException(NSExceptionCode code, NSExceptionParams params) {
    super(messageFromCode(code, params));
    this.code = code;
  }
  public NSExceptionCode getCode() { return code; }

  private static String messageFromCode(NSExceptionCode code, NSExceptionParams params) {
    switch(code) {
      case UnsupportedDomain: {
        return params.domain + " is unsupported";
      }
      case UnregisteredDomain: {
        return params.domain + " is not registered";
      }
      case UnknownCurrency: {
        return params.domain + " doesn't have such " + params.coinTicker + " configured";
      }
      case RecordNotFound: {
        return params.domain + " doesn't have " + params.record + "record";
      }
      case BlockchainIsDown: {
        return params.namingService + " blockchain network is down";
      }
      case IncorrectContractAddress: {
        return "used incorrect contract Address " + params.contractAddress;
      }
      case UnspecifiedResolver: {
        return "resolver was not set for " + params.domain;
      }
      case UnsupportedCurrency: {
        return "Currency " + params.coinTicker + " is not supported";
      }
      case NotImplemented: {
        return "Method " + params.methodName + "is not implemented for this naming service: " + params.namingService;
      }
      case InvalidDomain: {
        return "Domain: " + params.domain + " does not passes the following regex pattern ^[.a-z\\d-]+$ ";
      }
      case UnknownError:
      default: 
        return "Unknown Error occurred";
    }
  }

}