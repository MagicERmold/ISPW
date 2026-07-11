package com.stocktrack.pattern.singleton;

import com.stocktrack.bean.RuoloUtente;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Session {

    private static final ZoneId APPLICATION_ZONE = ZoneId.systemDefault();

    private final String id;
    private final String idUtente;
    private final RuoloUtente ruolo;
    private volatile LocalDateTime ultimoAccesso;
    private volatile boolean active;

    Session(String id, String idUtente, RuoloUtente ruolo, LocalDateTime dataLogin) {
        this.id = id;
        this.idUtente = idUtente;
        this.ruolo = ruolo;
        this.ultimoAccesso = dataLogin;
        this.active = true;
    }

    public void touch() {
        ultimoAccesso = LocalDateTime.now(APPLICATION_ZONE);
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

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getUltimoAccesso() {
        return ultimoAccesso;
    }

    public void setUltimoAccesso(LocalDateTime ultimoAccesso) {
        this.ultimoAccesso = ultimoAccesso;
    }
}
