package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

/**
 * Bean di input BCE che identifica il prodotto selezionato dalla view per le operazioni del controller.
 */
public class ProdottoSelezionatoBean {

    private String idProdotto;

    public ProdottoSelezionatoBean() {
    }

    public ProdottoSelezionatoBean(String idProdotto) {
        this.idProdotto = idProdotto;
    }

    public void validate() throws InvalidInputException {
        if (idProdotto == null || idProdotto.isBlank()) {
            throw new InvalidInputException("Selezionare un prodotto");
        }
    }

    public String getIdProdotto() {
        return idProdotto;
    }

}
