package com.stocktrack.bean;

public class StockBean {
    private String nome;
    private int quantity;
    private int soglia;

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
