package com.stocktrack.exception;

public class InvalidProductDataException extends Exception {
    public InvalidProductDataException(String message) {
        super(message);
    }
}