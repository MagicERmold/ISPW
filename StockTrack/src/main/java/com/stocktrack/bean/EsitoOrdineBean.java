package com.stocktrack.bean;

public class EsitoOrdineBean {

    private boolean successo;
    private String idOrdine;
    private String messaggio;

    public EsitoOrdineBean() {
    }

    public EsitoOrdineBean(boolean successo, String idOrdine, String messaggio) {
        this.successo = successo;
        this.idOrdine = idOrdine;
        this.messaggio = messaggio;
    }

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public String getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(String idOrdine) {
        this.idOrdine = idOrdine;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
