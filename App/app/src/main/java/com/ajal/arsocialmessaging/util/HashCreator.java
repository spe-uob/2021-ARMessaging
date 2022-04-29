package com.ajal.arsocialmessaging.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// REFERENCE: https://reflectoring.io/creating-hashes-in-java/ 07/04/2022 12:53

public class HashCreator {

    public static String createSHAHash(String input) {

        String hashtext = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest =
                    md.digest(input.getBytes(StandardCharsets.UTF_8));

            hashtext = convertToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;
    }

    private static String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}

