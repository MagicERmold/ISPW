package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

public class Titolare extends AbstractPersonaData {

    private String passwordHash;

    public Titolare() {
    }

    public Titolare(String id, String nome, String cognome, String email, String passwordHash) {
        super(id, nome, cognome, email);
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

}
