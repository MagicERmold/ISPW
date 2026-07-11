package com.stocktrack.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventario {

    private String id;
    private List<Prodotto> prodotti = new ArrayList<>();

    public Inventario() {
    }

    public Inventario(String id, List<Prodotto> prodotti) {
        this.id = id;
        setProdotti(prodotti);
    }

    public void aggiungiProdotto(Prodotto prodotto) {
        prodotti.add(prodotto);
    }

    public Optional<Prodotto> cercaProdotto(String idProdotto) {
        return prodotti.stream()
                .filter(prodotto -> prodotto.getId() != null && prodotto.getId().equals(idProdotto))
                .findFirst();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Prodotto> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }
}
