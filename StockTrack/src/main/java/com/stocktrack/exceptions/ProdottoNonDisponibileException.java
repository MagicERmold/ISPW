package com.stocktrack.exceptions;

public class ProdottoNonDisponibileException extends Exception {

    public ProdottoNonDisponibileException(String message) {
        super(message);
    }

    public ProdottoNonDisponibileException(String message, Throwable cause) {
        super(message, cause);
    }
}
