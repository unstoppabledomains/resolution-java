package com.unstoppabledomains.resolution;

public class NamingServiceException extends Exception {

  private NSExceptionCode code;
  private String detailMessage;
  
  NamingServiceException(NSExceptionCode code, String domain) {
    super(messageFromCode(code, domain));
    this.code = code;
    this.detailMessage = messageFromCode(code, domain);
  }

  public NSExceptionCode getCode() { return this.code; }

  public String getMessage() { return this.detailMessage; }

  private static String messageFromCode(NSExceptionCode code, String domainOrMessage) {
    if (domainOrMessage == null) domainOrMessage = "Domain";
    switch(code) {
      case UnsupportedDomain: {
        return domainOrMessage + " is unsupported";
      }
      case UnregisteredDomain: {
        return domainOrMessage + " is not registered";
      }
      case UnknownCurrency: {
        return domainOrMessage + " doesn't have such currency configured";
      }
      case RecordNotFound: {
        return domainOrMessage + " doesn't have such record";
      }
      case BlockchainIsDown: {
        return domainOrMessage + " blockchain network is down";
      }
      case UnknownError: {
        return "Something went wrong with \n" + domainOrMessage;
      }
      default: 
        return "";
    }
  }
}