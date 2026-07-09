package com.stocktrack.entity;

public class Titolare {

    private String id;
    private String nome;
    private String cognome;
    private String email;
    private String passwordHash;

    public Titolare() {
    }

    public Titolare(String id, String nome, String cognome, String email) {
        this(id, nome, cognome, email, null);
    }

    public Titolare(String id, String nome, String cognome, String email, String passwordHash) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
