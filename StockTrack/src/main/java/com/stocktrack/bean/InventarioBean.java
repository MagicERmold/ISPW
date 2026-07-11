package com.stocktrack.bean;

import java.util.ArrayList;
import java.util.List;

public class InventarioBean {

    private List<ProdottoBean> prodotti = new ArrayList<>();

    public InventarioBean() {
    }

    public InventarioBean(List<ProdottoBean> prodotti) {
        setProdotti(prodotti);
    }

    public List<ProdottoBean> getProdotti() {
        return new ArrayList<>(prodotti);
    }

    public void setProdotti(List<ProdottoBean> prodotti) {
        this.prodotti = prodotti == null ? new ArrayList<>() : new ArrayList<>(prodotti);
    }
}
