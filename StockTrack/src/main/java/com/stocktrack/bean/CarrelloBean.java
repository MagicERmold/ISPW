package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarrelloBean {

    private List<ProdottoBean> prodotti = new ArrayList<>();
    private BigDecimal totaleStimato = BigDecimal.ZERO;
    private boolean successo = true;
    private String messaggio = "";

    public CarrelloBean() {
    }

    public CarrelloBean(List<ProdottoBean> prodotti, BigDecimal totaleStimato) {
        setProdotti(prodotti);
        this.totaleStimato = totaleStimato;
        this.successo = true;
        this.messaggio = "Carrello configurato";
    }

    public CarrelloBean(boolean successo, String messaggio) {
        this.successo = successo;
        this.messaggio = messaggio;
    }

    public void validate() throws InvalidInputException {
        if (prodotti.isEmpty()) {
            throw new InvalidInputException("Carrello vuoto");
        }
    }

    public List<ProdottoBean> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<ProdottoBean> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }

    public BigDecimal getTotaleStimato() {
        return totaleStimato;
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
