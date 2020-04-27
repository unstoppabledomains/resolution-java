package com.unstoppabledomains.exceptions;

public class NamingServiceException extends Exception {
  private static final long serialVersionUID = 1L;
  private NSExceptionCode code;
  private NSExceptionParams params;
  private Throwable cause;

  public NamingServiceException(NSExceptionCode code, NSExceptionParams params, Throwable cause) {
    super(messageFromCode(code, params), cause);
    this.code = code;
    this.params = params;
    this.cause = cause;
  }
  public NamingServiceException(NSExceptionCode code, NSExceptionParams params) {
    super(messageFromCode(code, params));
    this.code = code;
    this.params = params;
    this.cause = null;
  }
  public NSExceptionCode getCode() { return this.code; }
  public String getMessage() { return messageFromCode(this.code, this.params); }

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
        return params.domain + " doesn't have such record";
      }
      case BlockchainIsDown: {
        return params.namingService + " blockchain network is down";
      }
      case UnknownError:
      default: 
        return "Unknown Error occured";
    }
  }

}