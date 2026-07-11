package com.stocktrack.bean;

import com.stocktrack.common.AbstractPersonaData;
import com.stocktrack.exceptions.InvalidInputException;

public class RegistrazioneBean extends AbstractPersonaData {

    private String password;
    private RuoloUtente ruolo = RuoloUtente.TITOLARE;

    public RegistrazioneBean() {
    }

    public RegistrazioneBean(String nome, String cognome, String email, String password, RuoloUtente ruolo) {
        super(null, nome, cognome, email);
        this.password = password;
        this.ruolo = ruolo;
    }

    public void validate() throws InvalidInputException {
        if (isBlank(getNome())) {
            throw new InvalidInputException("Nome obbligatorio");
        }
        if (isBlank(getEmail())) {
            throw new InvalidInputException("Email obbligatoria");
        }
        if (!getEmail().contains("@")) {
            throw new InvalidInputException("Email non valida");
        }
        if (isBlank(password) || password.length() < 8) {
            throw new InvalidInputException("Password troppo corta");
        }
        if (ruolo == null) {
            throw new InvalidInputException("Ruolo utente obbligatorio");
        }
    }

    public String getPassword() {
        return password;
    }

    public RuoloUtente getRuolo() {
        return ruolo;
    }

}
