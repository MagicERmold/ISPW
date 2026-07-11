package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

public class QuantitaProdottoBean {

    private String idProdotto;
    private int quantita;

    public QuantitaProdottoBean() {
    }

    public QuantitaProdottoBean(String idProdotto, int quantita) {
        this.idProdotto = idProdotto;
        this.quantita = quantita;
    }

    public void validate() throws InvalidInputException {
        if (idProdotto == null || idProdotto.isBlank()) {
            throw new InvalidInputException("Selezionare un prodotto");
        }
        if (quantita < 0) {
            throw new InvalidInputException("Quantita prodotto non valida");
        }
    }

    public void validateMovimento() throws InvalidInputException {
        validate();
        if (quantita <= 0) {
            throw new InvalidInputException("Quantita movimento non valida");
        }
    }

    public String getIdProdotto() {
        return idProdotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
}
