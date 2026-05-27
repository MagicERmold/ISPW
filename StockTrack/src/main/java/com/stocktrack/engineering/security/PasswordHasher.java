package com.stocktrack.engineering.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility per salvare nuove password in forma hash e mantenere compatibilita
 * con eventuali credenziali legacy ancora presenti in chiaro.
 */
public final class PasswordHasher {
    private static final String PREFIX = "SHA256:";

    private PasswordHasher() {
    }

    /**
     * Calcola l'hash SHA-256 della password e lo marca con un prefisso riconoscibile.
     */
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

    /**
     * Confronta la password inserita con quella salvata, supportando sia hash sia valori legacy.
     */
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
