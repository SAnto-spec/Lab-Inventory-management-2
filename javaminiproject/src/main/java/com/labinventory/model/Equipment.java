package com.labinventory.model;

import java.time.LocalDate;

public class Equipment {
    private int id;
    private String name;
    private String category;
    private int quantity;
    private int lowerLimit;
    private double unitPrice;
    private LocalDate expiryDate;
    private String location;
    private String supplier;
    private LocalDate dateAdded;

    public Equipment() {}

    public Equipment(int id, String name, String category, int quantity, int lowerLimit, 
                    double unitPrice, LocalDate expiryDate, String location, String supplier, LocalDate dateAdded) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.lowerLimit = lowerLimit;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
        this.location = location;
        this.supplier = supplier;
        this.dateAdded = dateAdded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isLowStock() {
        return quantity <= lowerLimit;
    }

    public boolean isNearExpiry() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate fifteenDaysFromNow = LocalDate.now().plusDays(15);
        return expiryDate.isBefore(fifteenDaysFromNow) || expiryDate.isEqual(fifteenDaysFromNow);
    }

    public int getDaysUntilExpiry() {
        if (expiryDate == null) {
            return Integer.MAX_VALUE;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", lowerLimit=" + lowerLimit +
                '}';
    }
}
