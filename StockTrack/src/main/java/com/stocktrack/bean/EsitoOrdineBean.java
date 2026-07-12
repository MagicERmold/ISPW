package com.stocktrack.bean;

/**
 * Bean di risposta BCE che comunica alla boundary e alla view l'esito della conferma di un ordine.
 */
public class EsitoOrdineBean {

    private boolean successo;
    private String messaggio;

    public EsitoOrdineBean() {
    }

    public EsitoOrdineBean(boolean successo, String messaggio) {
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
