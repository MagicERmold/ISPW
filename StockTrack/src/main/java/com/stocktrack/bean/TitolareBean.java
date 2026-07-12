package com.stocktrack.bean;

import com.stocktrack.common.AbstractPersonaData;
import com.stocktrack.exceptions.InvalidInputException;

/**
 * Bean BCE che rappresenta i dati del titolare scambiati tra boundary e controller senza esporre l'entity.
 */
public class TitolareBean extends AbstractPersonaData {

    public void validate() throws InvalidInputException {
        if (isBlank(getId())) {
            throw new InvalidInputException("Id titolare obbligatorio");
        }
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome titolare obbligatorio");
        }
    }
}
