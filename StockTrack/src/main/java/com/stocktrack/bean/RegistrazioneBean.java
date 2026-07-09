package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

public class RegistrazioneBean {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private RuoloUtente ruolo = RuoloUtente.TITOLARE;

    public RegistrazioneBean() {
    }

    public RegistrazioneBean(String nome, String cognome, String email, String password, RuoloUtente ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(nome)) {
            throw new InvalidInputException("Nome obbligatorio");
        }
        if (isBlank(email)) {
            throw new InvalidInputException("Email obbligatoria");
        }
        if (!email.contains("@")) {
            throw new InvalidInputException("Email non valida");
        }
        if (isBlank(password) || password.length() < 8) {
            throw new InvalidInputException("Password troppo corta");
        }
        if (ruolo == null) {
            throw new InvalidInputException("Ruolo utente obbligatorio");
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RuoloUtente getRuolo() {
        return ruolo;
    }

    public void setRuolo(RuoloUtente ruolo) {
        this.ruolo = ruolo;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
