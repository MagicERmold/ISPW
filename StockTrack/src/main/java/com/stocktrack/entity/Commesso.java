package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

public class Commesso extends AbstractPersonaData {

    private String passwordHash;

    public Commesso() {
    }

    public Commesso(String id, String nome, String cognome, String email) {
        this(id, nome, cognome, email, null);
    }

    public Commesso(String id, String nome, String cognome, String email, String passwordHash) {
        super(id, nome, cognome, email);
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
