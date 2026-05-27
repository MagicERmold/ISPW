package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Entity che rappresenta un prodotto del magazzino associato a uno specifico gruppo.
 */
public class Stock implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private int quantity;
    private final int threshold;
    private final String groupId;
    private final String category;

    public Stock(String name, int quantity, int threshold, String groupId, String category) {
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.groupId = groupId;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
    public int getThreshold() {
        return threshold;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() { return category; }

}
