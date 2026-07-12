package com.stocktrack.exceptions;

/**
 * Segnala un problema nel gateway del fornitore che la boundary presenta alla view come errore applicativo.
 */
public class FornitoreConnectionException extends Exception {

    public FornitoreConnectionException(String message) {
        super(message);
    }

    public FornitoreConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
