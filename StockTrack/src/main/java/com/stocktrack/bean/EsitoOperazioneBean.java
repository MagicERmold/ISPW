package com.stocktrack.bean;

public class EsitoOperazioneBean {

    private boolean successo;
    private String messaggio;

    public EsitoOperazioneBean() {
    }

    public EsitoOperazioneBean(boolean successo, String messaggio) {
        this.successo = successo;
        this.messaggio = messaggio;
    }

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
