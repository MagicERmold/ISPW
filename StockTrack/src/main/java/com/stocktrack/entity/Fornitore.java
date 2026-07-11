package com.stocktrack.entity;

import com.stocktrack.common.AbstractAnagraficaData;

public class Fornitore extends AbstractAnagraficaData {

    private String apiEndpoint;
    private boolean disponibile;
    private String passwordHash;

    public Fornitore() {
    }

    public Fornitore(String id, String nome, String email, String apiEndpoint, boolean disponibile) {
        this(id, nome, email, apiEndpoint, disponibile, "");
    }

    public Fornitore(String id, String nome, String email, String apiEndpoint, boolean disponibile,
                     String passwordHash) {
        super(id, nome, email);
        this.apiEndpoint = apiEndpoint;
        this.disponibile = disponibile;
        this.passwordHash = passwordHash;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
