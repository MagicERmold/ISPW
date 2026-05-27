package com.stocktrack.engineering.exception;

/**
 * Eccezione applicativa lanciata quando l'utente corrente non ha i permessi richiesti.
 */
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
