package com.stocktrack.model;

public class Stock {
    private final String nome;
    private int quantity;
    private int soglia;
    private String groupUid;

    public Stock(String nome, int quantity, int soglia, String groupUid) {
        this.nome = nome;
        this.quantity = quantity;
        this.soglia = soglia;
        this.groupUid = groupUid;
    }

    // GETTER e SETTER necessari
    public String getNome() {
        return nome;
    }

    public int getQuantity() {
        return quantity;
    }
    public int getSoglia() {
        return soglia;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}