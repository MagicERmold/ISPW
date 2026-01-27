package com.stocktrack.model;

import java.io.Serial;
import java.io.Serializable;

public class Stock implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private int quantity;
    private int threshold;
    private String groupId;
    private String category;

    public Stock(String name, int quantity, int threshold, String groupId, String category) {
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.groupId = groupId;
        this.category = category;
    }

    // GETTER e SETTER necessari
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
    public void setCategory(String category) { this.category = category; }

}