package com.stocktrack.bean;

/**
 * Bean usato dalla Boundary per mostrare e inviare dati relativi ai prodotti.
 */
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

    /**
     * Indica se il prodotto e sotto la soglia minima configurata.
     */
    public boolean isBelowThreshold() {
        return quantity < threshold;
    }

    /**
     * Indica se la quantita disponibile e pari a zero.
     */
    public boolean isEmpty() {
        return quantity == 0;
    }

    /**
     * Calcola quante unita mancano per raggiungere la soglia minima.
     */
    public int getMissingQuantity() {
        return Math.max(threshold - quantity, 0);
    }

    /**
     * Restituisce lo stato sintetico del prodotto per la visualizzazione in tabella.
     */
    public String getStatus() {
        if (isEmpty()) {
            return "Esaurito";
        }
        if (isBelowThreshold()) {
            return "Sotto soglia";
        }
        return "Disponibile";
    }
}
