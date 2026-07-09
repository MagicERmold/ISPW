package com.stocktrack.exceptions;

public class FornitoreConnectionException extends Exception {

    public FornitoreConnectionException(String message) {
        super(message);
    }

    public FornitoreConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
