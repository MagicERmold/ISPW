package com.stocktrack.entity;

import com.stocktrack.common.AbstractAnagraficaData;

public class Fornitore extends AbstractAnagraficaData {

    private String apiEndpoint;
    private boolean disponibile;

    public Fornitore() {
    }

    public Fornitore(String id, String nome, String email, String apiEndpoint, boolean disponibile) {
        super(id, nome, email);
        this.apiEndpoint = apiEndpoint;
        this.disponibile = disponibile;
    }

    public void notificaPagamento(String idOrdine) {
        // La notifica reale arrivera tramite gateway/controller applicativo.
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }
}
