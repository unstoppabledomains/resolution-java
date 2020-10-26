package com.unstoppabledomains.util;

import com.unstoppabledomains.resolution.artifacts.Numeric;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class Utilities {
  private Utilities() {}

  public static boolean isNull(String value) {
    return ( value == null ||
    value.equals("0x0000000000000000000000000000000000000000") ||
    value.equals("0x0") ||
    value.equals("") ||
    value.length() == 0);
  }

  public static String sha256(String key) {
    return sha256(key, false);
  }

  public static String sha256(String key, boolean hexEncoding) {
    SHA256Digest digester = new SHA256Digest();
    byte[] retValue = new byte[digester.getDigestSize()];
    if (hexEncoding) {
        digester.update(Numeric.hexStringToByteArray(key), 0, Numeric.hexStringToByteArray(key).length);
    } else {
        digester.update(key.getBytes(), 0, key.length());
    }
    digester.doFinal(retValue, 0);
    String result = Numeric.toHexString(retValue);
    return result.replace("0x", "");
  }
}