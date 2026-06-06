package com.stocktrack.bean;

import java.util.Locale;

/**
 * Bean usato dalla Boundary per mostrare e inviare dati relativi ai prodotti.
 */
public class StockBean {
    private String nome;
    private int quantity;
    private int threshold;
    private String category;

    public StockBean(String nome, int quantity, int threshold) {
        this(nome, quantity, threshold, "Generico");
    }

    public StockBean(String nome, int quantity, int threshold, String category) {
        setNome(nome);
        setQuantity(quantity);
        setThreshold(threshold);
        setCategory(category);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = normalizeRequiredText(nome, "Il nome del prodotto non puo essere vuoto.");
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantita non puo essere negativa.");
        }
        this.quantity = quantity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("La soglia non puo essere negativa.");
        }
        this.threshold = threshold;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = normalizeRequiredText(category, "La categoria non puo essere vuota.");
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
