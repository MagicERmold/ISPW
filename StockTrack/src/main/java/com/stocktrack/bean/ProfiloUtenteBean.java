package com.stocktrack.bean;

/**
 * Bean di risposta BCE restituito dopo l'autenticazione per mostrare alla view profilo e ruolo dell'utente.
 */
public class ProfiloUtenteBean {

    private String nome;
    private RuoloUtente ruolo;

    public ProfiloUtenteBean() {
    }

    public ProfiloUtenteBean(String nome, RuoloUtente ruolo) {
        this.nome = nome;
        this.ruolo = ruolo;
    }

    public boolean isTitolare() {
        return RuoloUtente.TITOLARE.equals(ruolo);
    }

    public boolean isCommesso() {
        return RuoloUtente.COMMESSO.equals(ruolo);
    }

    public boolean isFornitore() {
        return RuoloUtente.FORNITORE.equals(ruolo);
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

}
