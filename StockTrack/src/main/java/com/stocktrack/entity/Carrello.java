package com.stocktrack.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Carrello {

    private List<Prodotto> prodotti = new ArrayList<>();

    public Carrello() {
    }

    public Carrello(List<Prodotto> prodotti) {
        setProdotti(prodotti);
    }

    public void aggiungiProdotto(Prodotto prodotto) {
        prodotti.add(prodotto);
    }

    public boolean isVuoto() {
        return prodotti.isEmpty();
    }

    public BigDecimal calcolaTotale() {
        return prodotti.stream()
                .map(Prodotto::getPrezzoUnitario)
                .filter(prezzo -> prezzo != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Prodotto> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }
}
