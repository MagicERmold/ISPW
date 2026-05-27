package com.stocktrack.engineering.exception;

/**
 * Eccezione applicativa usata per uniformare gli errori provenienti dal layer di persistenza.
 */
public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
