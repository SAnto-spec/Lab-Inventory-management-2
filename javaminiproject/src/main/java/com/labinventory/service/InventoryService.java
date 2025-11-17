package com.labinventory.service;

import com.labinventory.database.EquipmentDAO;
import com.labinventory.database.OrderDAO;
import com.labinventory.model.Equipment;
import com.labinventory.model.Order;

import java.util.List;

public class InventoryService {
    private final EquipmentDAO equipmentDAO;
    private final OrderDAO orderDAO;

    public InventoryService() {
        this.equipmentDAO = new EquipmentDAO();
        this.orderDAO = new OrderDAO();
    }

    public List<Equipment> getAllEquipment() {
        return equipmentDAO.getAllEquipment();
    }

    public Equipment getEquipmentById(int id) {
        return equipmentDAO.getEquipmentById(id);
    }

    public boolean addEquipment(Equipment equipment) {
        return equipmentDAO.addEquipment(equipment);
    }

    public boolean updateEquipment(Equipment equipment) {
        return equipmentDAO.updateEquipment(equipment);
    }

    public boolean deleteEquipment(int id) {
        return equipmentDAO.deleteEquipment(id);
    }

    public List<Equipment> searchEquipment(String searchTerm) {
        return equipmentDAO.searchEquipment(searchTerm);
    }

    public List<Equipment> getLowStockAlerts() {
        return equipmentDAO.getLowStockEquipment();
    }

    public List<Equipment> getExpiryAlerts() {
        return equipmentDAO.getEquipmentNearExpiry();
    }

    public int getLowStockCount() {
        return getLowStockAlerts().size();
    }

    public int getExpiryAlertCount() {
        return getExpiryAlerts().size();
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public List<Order> getActiveOrders() {
        return orderDAO.getActiveOrders();
    }

    public Order getOrderById(int id) {
        return orderDAO.getOrderById(id);
    }

    public boolean addOrder(Order order) {
        return orderDAO.addOrder(order);
    }

    public boolean updateOrder(Order order) {
        return orderDAO.updateOrder(order);
    }

    public boolean deleteOrder(int id) {
        return orderDAO.deleteOrder(id);
    }

    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        return orderDAO.updateOrderStatus(orderId, newStatus);
    }

    public boolean markOrderAsDelivered(int orderId, int equipmentId, int quantity) {
        boolean orderUpdated = orderDAO.markAsDelivered(orderId);
        
        if (orderUpdated) {
            Equipment equipment = equipmentDAO.getEquipmentById(equipmentId);
            if (equipment != null) {
                equipment.setQuantity(equipment.getQuantity() + quantity);
                return equipmentDAO.updateEquipment(equipment);
            }
        }
        return false;
    }

    public int getActiveOrderCount() {
        return getActiveOrders().size();
    }

    public int getTotalEquipmentTypes() {
        return getAllEquipment().size();
    }

    public int getTotalEquipmentQuantity() {
        return getAllEquipment().stream()
                .mapToInt(Equipment::getQuantity)
                .sum();
    }
}
