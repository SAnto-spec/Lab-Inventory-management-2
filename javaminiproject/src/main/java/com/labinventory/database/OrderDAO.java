package com.labinventory.database;

import com.labinventory.model.Order;
import com.labinventory.model.Order.OrderStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                orderList.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Order> getActiveOrders() {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE status IN ('PENDING', 'IN_TRANSIT') ORDER BY expected_delivery_date";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                orderList.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;
    }

    public Order getOrderById(int id) {
        String query = "SELECT * FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching order by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addOrder(Order order) {
        String query = "INSERT INTO orders (equipment_id, equipment_name, quantity, order_date, " +
                      "expected_delivery_date, actual_delivery_date, status, supplier, total_cost) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, order.getEquipmentId());
            pstmt.setString(2, order.getEquipmentName());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setString(4, order.getOrderDate() != null ? order.getOrderDate().toString() : LocalDate.now().toString());
            pstmt.setString(5, order.getExpectedDeliveryDate() != null ? order.getExpectedDeliveryDate().toString() : null);
            pstmt.setString(6, order.getActualDeliveryDate() != null ? order.getActualDeliveryDate().toString() : null);
            pstmt.setString(7, order.getStatus().name());
            pstmt.setString(8, order.getSupplier());
            pstmt.setDouble(9, order.getTotalCost());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrder(Order order) {
        String query = "UPDATE orders SET equipment_id = ?, equipment_name = ?, quantity = ?, " +
                      "order_date = ?, expected_delivery_date = ?, actual_delivery_date = ?, " +
                      "status = ?, supplier = ?, total_cost = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, order.getEquipmentId());
            pstmt.setString(2, order.getEquipmentName());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setString(4, order.getOrderDate() != null ? order.getOrderDate().toString() : null);
            pstmt.setString(5, order.getExpectedDeliveryDate() != null ? order.getExpectedDeliveryDate().toString() : null);
            pstmt.setString(6, order.getActualDeliveryDate() != null ? order.getActualDeliveryDate().toString() : null);
            pstmt.setString(7, order.getStatus().name());
            pstmt.setString(8, order.getSupplier());
            pstmt.setDouble(9, order.getTotalCost());
            pstmt.setInt(10, order.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrder(int id) {
        String query = "DELETE FROM orders WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newStatus.name());
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAsDelivered(int orderId) {
        String query = "UPDATE orders SET status = 'DELIVERED', actual_delivery_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, orderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error marking order as delivered: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setEquipmentId(rs.getInt("equipment_id"));
        order.setEquipmentName(rs.getString("equipment_name"));
        order.setQuantity(rs.getInt("quantity"));
        
        String orderDateStr = rs.getString("order_date");
        if (orderDateStr != null && !orderDateStr.isEmpty()) {
            order.setOrderDate(LocalDate.parse(orderDateStr));
        }
        
        String expectedDeliveryStr = rs.getString("expected_delivery_date");
        if (expectedDeliveryStr != null && !expectedDeliveryStr.isEmpty()) {
            order.setExpectedDeliveryDate(LocalDate.parse(expectedDeliveryStr));
        }
        
        String actualDeliveryStr = rs.getString("actual_delivery_date");
        if (actualDeliveryStr != null && !actualDeliveryStr.isEmpty()) {
            order.setActualDeliveryDate(LocalDate.parse(actualDeliveryStr));
        }
        
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setSupplier(rs.getString("supplier"));
        order.setTotalCost(rs.getDouble("total_cost"));
        
        return order;
    }
}
