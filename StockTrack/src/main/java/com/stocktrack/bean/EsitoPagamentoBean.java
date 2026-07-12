package com.stocktrack.bean;

/**
 * Bean di risposta BCE che comunica al controller chiamante l'esito dell'autorizzazione di pagamento.
 */
public class EsitoPagamentoBean {

    private boolean successo;
    private String messaggio;

    public EsitoPagamentoBean() {
    }

    public EsitoPagamentoBean(boolean successo, String messaggio) {
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
