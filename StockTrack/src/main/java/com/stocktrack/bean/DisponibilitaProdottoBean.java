package com.stocktrack.bean;

/**
 * Bean di risposta BCE usato dal controller per comunicare alla boundary la disponibilita di un prodotto.
 */
public class DisponibilitaProdottoBean {

    private ProdottoBean prodotto;
    private int quantitaDisponibile;
    private boolean disponibile;
    private String messaggio;

    public DisponibilitaProdottoBean() {
    }

    public DisponibilitaProdottoBean(ProdottoBean prodotto, int quantitaDisponibile, boolean disponibile,
                                    String messaggio) {
        this.prodotto = prodotto;
        this.quantitaDisponibile = quantitaDisponibile;
        this.disponibile = disponibile;
        this.messaggio = messaggio;
    }

    public ProdottoBean getProdotto() {
        return prodotto;
    }

    public void setProdotto(ProdottoBean prodotto) {
        this.prodotto = prodotto;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
