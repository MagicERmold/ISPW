package com.stocktrack.engineering.exception;

/**
 * Eccezione applicativa lanciata quando un'operazione richiede un utente non presente.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
