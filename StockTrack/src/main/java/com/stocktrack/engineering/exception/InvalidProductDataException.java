package com.stocktrack.engineering.exception;

/**
 * Eccezione applicativa lanciata quando i dati di un prodotto violano le regole di dominio.
 */
public class InvalidProductDataException extends Exception {
    public InvalidProductDataException(String message) {
        super(message);
    }
}
