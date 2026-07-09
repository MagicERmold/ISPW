package com.stocktrack.exceptions;

public class OperazioneNonAutorizzataException extends Exception {

    public OperazioneNonAutorizzataException(String message) {
        super(message);
    }

    public OperazioneNonAutorizzataException(String message, Throwable cause) {
        super(message, cause);
    }
}
