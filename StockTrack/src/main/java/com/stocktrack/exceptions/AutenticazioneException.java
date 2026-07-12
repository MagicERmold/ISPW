package com.stocktrack.exceptions;

/**
 * Segnala un errore di autenticazione che la boundary traduce in un messaggio destinato alla view.
 */
public class AutenticazioneException extends Exception {

    public AutenticazioneException(String message) {
        super(message);
    }

}
