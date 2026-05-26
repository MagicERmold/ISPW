package com.stocktrack.bean;

public class StockBean {
    private final String nome;
    private final int quantity;
    private final int threshold;
    private final String category;

    public StockBean(String nome, int quantity, int threshold) {
        this.nome = nome;
        this.quantity = quantity;
        this.threshold = threshold;
        this.category = "Generico";
    }

    public StockBean(String nome, int quantity, int threshold, String category) {
        this.nome = nome;
        this.quantity = quantity;
        this.threshold = threshold;
        this.category = category;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getCategory() {
        return category;
    }

    public boolean isBelowThreshold() {
        return quantity < threshold;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    public int getMissingQuantity() {
        return Math.max(threshold - quantity, 0);
    }

    public String getStatus() {
        if (isEmpty()) {
            return "Esaurito";
        }
        if (isBelowThreshold()) {
            return "Sottoscorta";
        }
        return "OK";
    }
}
