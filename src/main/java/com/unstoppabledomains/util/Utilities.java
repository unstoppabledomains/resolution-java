package com.unstoppabledomains.util;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;

import com.unstoppabledomains.resolution.artifacts.Numeric;

import org.bouncycastle.crypto.digests.SHA256Digest;

public class Utilities {
  private Utilities() {}

  public static boolean isEmptyResponse(String value) {
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

  /**
   * Converts a numerical token id to a namehash
   * @param tokenID a numerical token id
   * @return namehash from provided token id
   */
  public static String tokenIDToNamehash(BigInteger tokenID) {
    return String.format("0x%64s", tokenID.toString(16)).replace(' ', '0');
  }

  /**
   * Converts a namehash to numerical token id
   * @param namehash a namehash in "0x..." format
   * @return token id from provided namehash
   */
  public static BigInteger namehashToTokenID(String namehash) {
    return new BigInteger(namehash.substring(2), 16);
  }

  public static String convertEthAddress(BigInteger address) {
    return normalizeAddress(address.toString(16), 40);
  }

  public static String normalizeAddress(String address) {
    return normalizeAddress(address, 64);
  }

  public static String normalizeAddress(String address, int length) {
    String hexPrefix = "0x";
    String cleanAddress = address.startsWith(hexPrefix) ? address.substring(2) : address;
    if (cleanAddress.length() < length) {
      int amountOfZeroes = length - cleanAddress.length();
      String zeros = new String(new char[amountOfZeroes]).replace('\0', '0');
      return hexPrefix + zeros + cleanAddress;
    } 
    return hexPrefix + cleanAddress;
  }
  
  public static <T1, T2> void iterateSimultaneously(Iterable<T1> c1, Iterable<T2> c2, BiConsumer<T1, T2> consumer) {
    Iterator<T1> i1 = c1.iterator();
    Iterator<T2> i2 = c2.iterator();
    while (i1.hasNext() && i2.hasNext()) {
        consumer.accept(i1.next(), i2.next());
    }
  }

  public static <T> Set<T> combineTwoSets(Set<T> set1, Set<T> set2) {
    HashSet<T> set = new HashSet<>();
    set.addAll(set1);
    set.addAll(set2);
    return set;
  }
}