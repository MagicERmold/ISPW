package com.stocktrack.entity;

import java.util.ArrayList;
import java.util.List;

public class Carrello {

    private List<Prodotto> prodotti = new ArrayList<>();

    public Carrello() {
    }

    public Carrello(List<Prodotto> prodotti) {
        setProdotti(prodotti);
    }

    public List<Prodotto> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }
}
