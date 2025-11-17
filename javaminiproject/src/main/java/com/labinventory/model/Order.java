package com.labinventory.model;

import java.time.LocalDate;

public class Order {
    private int id;
    private int equipmentId;
    private String equipmentName;
    private int quantity;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private OrderStatus status;
    private String supplier;
    private double totalCost;

    public enum OrderStatus {
        PENDING, IN_TRANSIT, DELIVERED, CANCELLED
    }

    public Order() {}

    public Order(int id, int equipmentId, String equipmentName, int quantity, 
                LocalDate orderDate, LocalDate expectedDeliveryDate, LocalDate actualDeliveryDate,
                OrderStatus status, String supplier, double totalCost) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.status = status;
        this.supplier = supplier;
        this.totalCost = totalCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isActiveOrder() {
        return status == OrderStatus.PENDING || status == OrderStatus.IN_TRANSIT;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", equipmentName='" + equipmentName + '\'' +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}
