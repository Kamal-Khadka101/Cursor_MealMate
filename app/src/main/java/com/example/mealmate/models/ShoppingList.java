package com.example.mealmate.models;

import java.util.List;
import java.util.ArrayList;

public class ShoppingList {
    private String id;
    private String userId;
    private String name;
    private List<ShoppingItem> items;
    private String delegatedTo;
    private boolean isCompleted;
    private long createdAt;

    public ShoppingList() {
        // Required empty constructor for Firebase
        items = new ArrayList<>();
    }

    public ShoppingList(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.items = new ArrayList<>();
        this.isCompleted = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ShoppingItem> getItems() { return items; }
    public void setItems(List<ShoppingItem> items) { this.items = items; }
    public String getDelegatedTo() { return delegatedTo; }
    public void setDelegatedTo(String delegatedTo) { this.delegatedTo = delegatedTo; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public static class ShoppingItem {
        private String name;
        private double quantity;
        private String unit;
        private boolean purchased;
        private double price;

        public ShoppingItem() {
            // Required empty constructor for Firebase
        }

        public ShoppingItem(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.purchased = false;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public boolean isPurchased() { return purchased; }
        public void setPurchased(boolean purchased) { this.purchased = purchased; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
} 