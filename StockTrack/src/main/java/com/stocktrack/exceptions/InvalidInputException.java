package com.stocktrack.exceptions;

/**
 * Segnala dati non validi rilevati da bean o controller e gestiti dalla boundary prima della risposta alla view.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
