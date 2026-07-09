package com.stocktrack.pattern.singleton;

import com.stocktrack.bean.RuoloUtente;

import java.time.LocalDateTime;

public class Session {

    private final String id;
    private final String idUtente;
    private final RuoloUtente ruolo;
    private final LocalDateTime dataLogin;
    private volatile LocalDateTime ultimoAccesso;
    private volatile boolean active;

    Session(String id, String idUtente, RuoloUtente ruolo, LocalDateTime dataLogin) {
        this.id = id;
        this.idUtente = idUtente;
        this.ruolo = ruolo;
        this.dataLogin = dataLogin;
        this.ultimoAccesso = dataLogin;
        this.active = true;
    }

    public void touch() {
        ultimoAccesso = LocalDateTime.now();
    }

    public void invalidate() {
        active = false;
    }

    public String getId() {
        return id;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public RuoloUtente getRuolo() {
        return ruolo;
    }

    public LocalDateTime getDataLogin() {
        return dataLogin;
    }

    public LocalDateTime getUltimoAccesso() {
        return ultimoAccesso;
    }

    public boolean isActive() {
        return active;
    }
}
