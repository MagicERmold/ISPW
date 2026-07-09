package com.stocktrack.entity;

public class Fornitore {

    private String id;
    private String nome;
    private String email;
    private String apiEndpoint;
    private boolean disponibile;

    public Fornitore() {
    }

    public Fornitore(String id, String nome, String email, String apiEndpoint, boolean disponibile) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.apiEndpoint = apiEndpoint;
        this.disponibile = disponibile;
    }

    public void notificaPagamento(String idOrdine) {
        // La notifica reale arrivera tramite gateway/controller applicativo.
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
