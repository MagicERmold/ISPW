package com.stocktrack.engineering.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private static final String PREFIX = "SHA256:";

    private PasswordHasher() {
    }

    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(PREFIX);
            for (byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 non disponibile", e);
        }
    }

    public static boolean matches(String plainPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (!storedPassword.startsWith(PREFIX)) {
            return storedPassword.equals(plainPassword);
        }
        return storedPassword.equals(hash(plainPassword));
    }
}
