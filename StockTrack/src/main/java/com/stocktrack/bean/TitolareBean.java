package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

public class TitolareBean {

    private String id;
    private String nome;
    private String cognome;
    private String email;

    public TitolareBean() {
    }

    public TitolareBean(String id, String nome, String cognome, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(id)) {
            throw new InvalidInputException("Id titolare obbligatorio");
        }
        if (isBlank(nome)) {
            throw new InvalidInputException("Nome titolare obbligatorio");
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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
