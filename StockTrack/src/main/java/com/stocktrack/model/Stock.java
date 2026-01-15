package com.stocktrack.model;

public class Stock {
    private String nome;
    private int quantity;
    private int soglia;

    public Stock(String nome, int quantity, int soglia) {
        this.nome = nome;
        this.quantity = quantity;
        this.soglia = soglia;
    }

    public String getNome() {
        return nome;
    }
}
