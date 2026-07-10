package com.stocktrack.bean;

import com.stocktrack.common.AbstractPersonaData;
import com.stocktrack.exceptions.InvalidInputException;

public class CommessoBean extends AbstractPersonaData {

    public CommessoBean() {
    }

    public CommessoBean(String id, String nome, String cognome, String email) {
        super(id, nome, cognome, email);
    }

    public void validate() throws InvalidInputException {
        if (isBlank(getId())) {
            throw new InvalidInputException("Id commesso obbligatorio");
        }
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome commesso obbligatorio");
        }
    }
}
