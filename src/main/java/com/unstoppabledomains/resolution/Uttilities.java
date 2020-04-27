package com.unstoppabledomains.resolution;

abstract class Utilities {
  String name;
  Boolean verbose;

  public Utilities(String name, Boolean verbose) {
    this.name = name;
    this.verbose = verbose;
  }

  protected boolean isNull(String value) {
    return (value.equals("0x0000000000000000000000000000000000000000") || value == null || value.equals("0x")
        || value.equals("") || value.length() == 0);
  }

  protected void log(String message) {
    if (this.verbose)
      System.out.println("[ " + this.name + " ] " + message);
  }
}