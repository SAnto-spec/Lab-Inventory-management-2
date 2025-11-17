package com.labinventory.database;

import com.labinventory.model.Equipment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipmentList = new ArrayList<>();
        String query = "SELECT * FROM equipments ORDER BY id ASC";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                equipmentList.add(extractEquipmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching equipment: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    public Equipment getEquipmentById(int id) {
        String query = "SELECT * FROM equipments WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractEquipmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching equipment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Equipment> getLowStockEquipment() {
        List<Equipment> equipmentList = new ArrayList<>();
        String query = "SELECT * FROM equipments WHERE quantity <= lower_limit ORDER BY quantity";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                equipmentList.add(extractEquipmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low stock equipment: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    public List<Equipment> getEquipmentNearExpiry() {
        List<Equipment> equipmentList = new ArrayList<>();
        LocalDate fifteenDaysFromNow = LocalDate.now().plusDays(15);
        String query = "SELECT * FROM equipments WHERE expiry_date IS NOT NULL AND expiry_date <= ? ORDER BY expiry_date";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, fifteenDaysFromNow.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                equipmentList.add(extractEquipmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching equipment near expiry: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    public boolean addEquipment(Equipment equipment) {
        String query = "INSERT INTO equipments (name, category, quantity, lower_limit, unit_price, " +
                      "expiry_date, location, supplier, date_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getCategory());
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setInt(4, equipment.getLowerLimit());
            pstmt.setDouble(5, equipment.getUnitPrice());
            pstmt.setString(6, equipment.getExpiryDate() != null ? equipment.getExpiryDate().toString() : null);
            pstmt.setString(7, equipment.getLocation());
            pstmt.setString(8, equipment.getSupplier());
            pstmt.setString(9, equipment.getDateAdded() != null ? equipment.getDateAdded().toString() : LocalDate.now().toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEquipment(Equipment equipment) {
        String query = "UPDATE equipments SET name = ?, category = ?, quantity = ?, lower_limit = ?, " +
                      "unit_price = ?, expiry_date = ?, location = ?, supplier = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getCategory());
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setInt(4, equipment.getLowerLimit());
            pstmt.setDouble(5, equipment.getUnitPrice());
            pstmt.setString(6, equipment.getExpiryDate() != null ? equipment.getExpiryDate().toString() : null);
            pstmt.setString(7, equipment.getLocation());
            pstmt.setString(8, equipment.getSupplier());
            pstmt.setInt(9, equipment.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEquipment(int id) {
        String query = "DELETE FROM equipments WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting equipment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Equipment> searchEquipment(String searchTerm) {
        List<Equipment> equipmentList = new ArrayList<>();
        String query = "SELECT * FROM equipments WHERE name LIKE ? OR category LIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                equipmentList.add(extractEquipmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching equipment: " + e.getMessage());
            e.printStackTrace();
        }
        return equipmentList;
    }

    private Equipment extractEquipmentFromResultSet(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setId(rs.getInt("id"));
        equipment.setName(rs.getString("name"));
        equipment.setCategory(rs.getString("category"));
        equipment.setQuantity(rs.getInt("quantity"));
        equipment.setLowerLimit(rs.getInt("lower_limit"));
        equipment.setUnitPrice(rs.getDouble("unit_price"));
        
        String expiryDateStr = rs.getString("expiry_date");
        if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
            equipment.setExpiryDate(LocalDate.parse(expiryDateStr));
        }
        
        equipment.setLocation(rs.getString("location"));
        equipment.setSupplier(rs.getString("supplier"));
        
        String dateAddedStr = rs.getString("date_added");
        if (dateAddedStr != null && !dateAddedStr.isEmpty()) {
            equipment.setDateAdded(LocalDate.parse(dateAddedStr));
        }
        
        return equipment;
    }
}
