package com.stocktrack.bean;

import com.stocktrack.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarrelloBean {

    private List<ProdottoBean> prodotti = new ArrayList<>();
    private BigDecimal totaleStimato = BigDecimal.ZERO;

    public CarrelloBean() {
    }

    public CarrelloBean(List<ProdottoBean> prodotti, BigDecimal totaleStimato) {
        setProdotti(prodotti);
        this.totaleStimato = totaleStimato;
    }

    public void validate() throws InvalidInputException {
        if (prodotti.isEmpty()) {
            throw new InvalidInputException("Carrello vuoto");
        }
    }

    public void addProdotto(ProdottoBean prodottoBean) {
        prodotti.add(prodottoBean);
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

    public void setTotaleStimato(BigDecimal totaleStimato) {
        this.totaleStimato = totaleStimato;
    }
}
