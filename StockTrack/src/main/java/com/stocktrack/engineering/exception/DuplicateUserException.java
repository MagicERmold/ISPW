package com.stocktrack.engineering.exception;

/**
 * Eccezione applicativa lanciata quando una registrazione usa uno username gia presente.
 */
public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }
}
