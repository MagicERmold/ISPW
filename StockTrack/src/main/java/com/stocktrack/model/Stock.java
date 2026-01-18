package com.stocktrack.model;

public class Stock {
    private String nome;
    private int quantity;
    private int soglia;
    private String groupUid; // CAMPO MANCANTE AGGIUNTO

    public Stock(String nome, int quantity, int soglia) {
        this(nome, quantity, soglia, null);
    }

    public Stock(String nome, int quantity, int soglia, String groupUid) {
        this.nome = nome;
        this.quantity = quantity;
        this.soglia = soglia;
        this.groupUid = groupUid;
    }

    // Rinominiamo/Aliasi per compatibilità col Controller
    public String getNome() { return nome; }

    public int getQuantity() { return quantity; }
    public int getSoglia() { return soglia; }

    public String getGroupUid() { return groupUid; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

}