package com.unstoppabledomains.resolution;

/** This file was taken from Web3j and adapted accordingly
 *  https://github.com/web3j/web3j/blob/master/core/src/main/java/org/web3j/ens/NameHash.java
 */

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.net.IDN;

import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;
import com.unstoppabledomains.resolution.artifacts.Hash;
import com.unstoppabledomains.resolution.artifacts.Numeric;


public class Namehash {
    private Namehash() {}

    private static final byte[] EMPTY = new byte[32];

    public static byte[] nameHashAsBytes(String domain) throws NamingServiceException {
        return Numeric.hexStringToByteArray(nameHash(domain));
    }

    public static String nameHash(String domain) throws NamingServiceException {
        String normaliseddomain = normalise(domain);
        return Numeric.toHexString(nameHash(normaliseddomain.split("\\.")));
    }

    private static byte[] nameHash(String[] labels) {
        if (labels.length == 0 || labels[0].equals("")) {
            return EMPTY;
        } else {
            String[] tail;
            if (labels.length == 1) {
                tail = new String[] {};
            } else {
                tail = Arrays.copyOfRange(labels, 1, labels.length);
            }

            byte[] remainderHash = nameHash(tail);
            byte[] result = Arrays.copyOf(remainderHash, 64);

            byte[] labelHash = Hash.sha3(labels[0].getBytes(StandardCharsets.UTF_8));
            System.arraycopy(labelHash, 0, result, 32, labelHash.length);

            return Hash.sha3(result);
        }
    }

    /**
     * Normalise blockchain name as per the <a
     * href="http://docs.ens.domains/en/latest/implementers.html#normalising-and-validating-names">specification</a>.
     *
     * @param domain our user input blockchain domain
     * @return normalised blockchain domain
     */
    public static String normalise(String domain) throws NamingServiceException {
        try {
            return IDN.toASCII(domain, IDN.USE_STD3_ASCII_RULES).toLowerCase();
        } catch (IllegalArgumentException e) {
            throw new NamingServiceException(NSExceptionCode.UnsupportedDomain, new NSExceptionParams("d", domain));
        }
    }
}
