package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

public class Titolare extends AbstractPersonaData {

    private String passwordHash;

    public Titolare() {
    }

    public Titolare(String id, String nome, String cognome, String email) {
        this(id, nome, cognome, email, null);
    }

    public Titolare(String id, String nome, String cognome, String email, String passwordHash) {
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
