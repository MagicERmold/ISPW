package com.stocktrack.bean;

import com.stocktrack.common.AbstractPersonaData;
import com.stocktrack.exceptions.InvalidInputException;

/**
 * Bean BCE che rappresenta i dati di un commesso scambiati tra boundary e controller, senza esporre l'entity.
 */
public class CommessoBean extends AbstractPersonaData {

    public void validate() throws InvalidInputException {
        if (isBlank(getId())) {
            throw new InvalidInputException("Id commesso obbligatorio");
        }
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome commesso obbligatorio");
        }
    }
}
