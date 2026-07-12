package com.stocktrack.exceptions;

/**
 * Segnala il fallimento del gateway di pagamento e permette al controller di restituire un esito BCE esplicito.
 */
public class PagamentoFallitoException extends Exception {

    public PagamentoFallitoException(String message) {
        super(message);
    }

    public PagamentoFallitoException(String message, Throwable cause) {
        super(message, cause);
    }
}
