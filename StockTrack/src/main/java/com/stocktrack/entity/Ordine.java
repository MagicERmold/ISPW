package com.stocktrack.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Ordine {

    private String id;
    private Fornitore fornitore;
    private List<Prodotto> prodotti = new ArrayList<>();
    private BigDecimal totale = BigDecimal.ZERO;
    private String stato;

    public Ordine() {
    }

    public Ordine(String id, Fornitore fornitore, List<Prodotto> prodotti, BigDecimal totale) {
        this.id = id;
        this.fornitore = fornitore;
        setProdotti(prodotti);
        this.totale = totale;
        this.stato = "CREATO";
    }

    public void marcaPagato() {
        stato = "PAGATO";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Fornitore getFornitore() {
        return fornitore;
    }

    public void setFornitore(Fornitore fornitore) {
        this.fornitore = fornitore;
    }

    public List<Prodotto> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }
}
