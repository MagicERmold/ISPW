package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

public class Commesso extends AbstractPersonaData {

    private String passwordHash;

    public Commesso() {
    }

    public Commesso(String id, String nome, String cognome, String email, String passwordHash) {
        super(id, nome, cognome, email);
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

}
