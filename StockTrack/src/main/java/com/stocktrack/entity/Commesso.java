package com.stocktrack.entity;

import com.stocktrack.common.AbstractPersonaData;

/**
 * Entity BCE che rappresenta un commesso persistito; viene usata dai controller applicativi e dai DAO.
 */
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
