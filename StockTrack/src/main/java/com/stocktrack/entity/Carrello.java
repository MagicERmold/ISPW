package com.stocktrack.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity BCE che rappresenta il carrello nel dominio; viene usata dai controller applicativi per gestirne i prodotti.
 */
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
