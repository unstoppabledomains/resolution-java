package com.unstoppabledomains.resolution;

class Utilities {
  static boolean isNull(String value) {
    return (value.equals("0x0000000000000000000000000000000000000000") || value == null || value.equals("0x")
        || value.equals("") || value.length() == 0);
  }
}