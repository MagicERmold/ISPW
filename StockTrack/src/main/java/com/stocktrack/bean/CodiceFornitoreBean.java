package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

public class CodiceFornitoreBean {

    private String codiceFornitore;

    public CodiceFornitoreBean() {
    }

    public CodiceFornitoreBean(String codiceFornitore) {
        this.codiceFornitore = codiceFornitore;
    }

    public void validate() throws InvalidInputException {
        if (codiceFornitore == null || codiceFornitore.isBlank()) {
            throw new InvalidInputException("Codice fornitore obbligatorio");
        }
    }

    public String getCodiceFornitore() {
        return codiceFornitore;
    }

    public void setCodiceFornitore(String codiceFornitore) {
        this.codiceFornitore = codiceFornitore;
    }
}
