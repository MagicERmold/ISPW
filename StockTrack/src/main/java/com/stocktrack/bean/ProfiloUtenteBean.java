package com.stocktrack.bean;

import java.time.LocalDateTime;

public class ProfiloUtenteBean {

    private String idUtente;
    private String nome;
    private RuoloUtente ruolo;
    private LocalDateTime dataLogin;

    public ProfiloUtenteBean() {
    }

    public ProfiloUtenteBean(String idUtente, String nome, RuoloUtente ruolo, LocalDateTime dataLogin) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.ruolo = ruolo;
        this.dataLogin = dataLogin;
    }

    public boolean isTitolare() {
        return RuoloUtente.TITOLARE.equals(ruolo);
    }

    public boolean isCommesso() {
        return RuoloUtente.COMMESSO.equals(ruolo);
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public RuoloUtente getRuolo() {
        return ruolo;
    }

    public void setRuolo(RuoloUtente ruolo) {
        this.ruolo = ruolo;
    }

    public LocalDateTime getDataLogin() {
        return dataLogin;
    }

    public void setDataLogin(LocalDateTime dataLogin) {
        this.dataLogin = dataLogin;
    }
}
