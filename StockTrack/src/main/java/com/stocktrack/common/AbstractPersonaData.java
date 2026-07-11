package com.stocktrack.common;

public abstract class AbstractPersonaData extends AbstractAnagraficaData {

    private String cognome;

    protected AbstractPersonaData() {
    }

    protected AbstractPersonaData(String id, String nome, String cognome, String email) {
        super(id, nome, email);
        this.cognome = cognome;
    }

    public String getCognome() {
        return cognome;
    }

}
