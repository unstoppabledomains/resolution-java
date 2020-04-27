package com.unstoppabledomains.exceptions;

public class NSExceptionParams {
  public String domain;
  public String namingService;
  public String coinTicker;

  public NSExceptionParams(String domain) {
    this.domain = domain;
    this.coinTicker = null;
    this.namingService = null;
  }

  public NSExceptionParams(String domain, String coinTicker) {
    this.domain = domain;
    this.coinTicker = coinTicker;
    this.namingService = null;
  }

  public NSExceptionParams(String domain, String coinTicker, String namingService) {
    this.domain = domain;
    this.coinTicker = coinTicker;
    this.namingService = namingService;
  }

}