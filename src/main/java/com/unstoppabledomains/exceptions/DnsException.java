package com.unstoppabledomains.exceptions;

public class DnsException extends Exception {
  private static final long serialVersionUID = 1L;
  private final DnsExceptionCode code;
  
  public DnsException(DnsExceptionCode code) {
    super(messageFromCode(code, new NSExceptionParams("", "")));
    this.code = code;
  }

  public DnsException(DnsExceptionCode code, NSExceptionParams params, Throwable cause) {
    super(messageFromCode(code, params), cause);
    this.code = code;
  }
  public DnsException(DnsExceptionCode code, NSExceptionParams params) {
    super(messageFromCode(code, params));
    this.code = code;
  }
  public DnsExceptionCode getCode() { return code; }

  private static String messageFromCode(DnsExceptionCode code, NSExceptionParams params) {
    switch(code) {
      case DnsRecordCorrupted: {
        return "record " + params.record + " is invalid json-string";
      }
      case InconsistentTtl: {
        return "ttl for record " + params.record + "is different for other records of the same type";
      }
      default: 
        return "Unknown Error occurred";
    }
  }

}