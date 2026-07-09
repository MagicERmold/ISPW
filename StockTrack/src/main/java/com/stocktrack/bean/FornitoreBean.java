package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

public class FornitoreBean {

    private String id;
    private String nome;
    private String email;
    private String apiEndpoint;
    private boolean disponibile;

    public FornitoreBean() {
    }

    public FornitoreBean(String id, String nome, String email, String apiEndpoint, boolean disponibile) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.apiEndpoint = apiEndpoint;
        this.disponibile = disponibile;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(id)) {
            throw new InvalidInputException("Id fornitore obbligatorio");
        }
        if (isBlank(nome)) {
            throw new InvalidInputException("Nome fornitore obbligatorio");
        }
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

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
