package com.stocktrack.bean;

/**
 * Bean di risposta BCE usato dai controller e dalle boundary per comunicare successo e messaggio alla view.
 */
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

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
