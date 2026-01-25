package com.stocktrack.bean;

public class StockBean {
    private final String nome;
    private final int quantity;
    private final int soglia;

    public StockBean(String nome, int quantity, int soglia) {
        this.nome = nome;
        this.quantity = quantity;
        this.soglia = soglia;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSoglia() {
        return soglia;
    }
}
