package com.stocktrack.bean;

public class EsitoPagamentoBean {

    private boolean successo;
    private String codiceAutorizzazione;
    private String messaggio;

    public EsitoPagamentoBean() {
    }

    public EsitoPagamentoBean(boolean successo, String codiceAutorizzazione, String messaggio) {
        this.successo = successo;
        this.codiceAutorizzazione = codiceAutorizzazione;
        this.messaggio = messaggio;
    }

    public boolean isSuccesso() {
        return successo;
    }

    public void setSuccesso(boolean successo) {
        this.successo = successo;
    }

    public String getCodiceAutorizzazione() {
        return codiceAutorizzazione;
    }

    public void setCodiceAutorizzazione(String codiceAutorizzazione) {
        this.codiceAutorizzazione = codiceAutorizzazione;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
