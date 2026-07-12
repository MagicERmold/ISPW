package com.stocktrack.exceptions;

/**
 * Segnala una disponibilita insufficiente rilevata dal controller e convertita dalla boundary in un esito per la view.
 */
public class ProdottoNonDisponibileException extends Exception {

    public ProdottoNonDisponibileException(String message) {
        super(message);
    }

}
