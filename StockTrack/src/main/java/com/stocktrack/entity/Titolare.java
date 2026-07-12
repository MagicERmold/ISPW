package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

/**
 * Entity BCE che rappresenta il titolare persistito; viene usata dal controller di login e dal relativo DAO.
 */
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
