package com.stocktrack.bean;

import com.stocktrack.common.AbstractAnagraficaData;
import com.stocktrack.exceptions.InvalidInputException;

public class FornitoreBean extends AbstractAnagraficaData {

    private String apiEndpoint;
    private boolean disponibile;

    public FornitoreBean() {
    }

    public FornitoreBean(String id, String nome, String email, String apiEndpoint, boolean disponibile) {
        super(id, nome, email);
        this.apiEndpoint = apiEndpoint;
        this.disponibile = disponibile;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(getId())) {
            throw new InvalidInputException("Id fornitore obbligatorio");
        }
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome fornitore obbligatorio");
        }
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

}
