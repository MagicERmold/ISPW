package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

/**
 * Bean BCE che trasporta e valida il codice fornitore inserito dalla view prima dell'uso nel controller.
 */
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

}
