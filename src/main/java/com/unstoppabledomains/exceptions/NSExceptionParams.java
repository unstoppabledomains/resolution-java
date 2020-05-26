package com.unstoppabledomains.exceptions;

import java.util.Arrays;

public class NSExceptionParams {
  public String domain;
  public String namingService;
  public String coinTicker;
  public String contractAddress;
  public String methodName;

  public NSExceptionParams(String format, String ...args) {
    String[] options = format.split("|");
    int index = 0;
    for (String option: options) {
      if (option.equals("|")) continue;
      parseOption(option, args[index++]);
    }
  }

  private void parseOption(String option, String value) {
    switch(option) {
      case "d": {
        this.domain = value;
        return ;
      }
      case "n": {
        this.namingService = value;
        return ;
      }
      case "c": {
        this.coinTicker = value;
        return;
      }
      case "a": {
        this.contractAddress = value;
        return;
      }
      case "m": {
        this.methodName = value;
        return;
      }
      default: {
        return ;
      }
    }
  }
}