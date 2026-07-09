package com.stocktrack.exceptions;

public class PagamentoFallitoException extends Exception {

    public PagamentoFallitoException(String message) {
        super(message);
    }

    public PagamentoFallitoException(String message, Throwable cause) {
        super(message, cause);
    }
}
