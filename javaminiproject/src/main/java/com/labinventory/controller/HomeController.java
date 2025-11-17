package com.labinventory.controller;

import com.labinventory.model.Equipment;
import com.labinventory.model.Order;
import com.labinventory.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HomeController {
    
    @FXML private Text lowStockCount;
    @FXML private Text expiryAlertCount;
    @FXML private Text activeOrderCount;
    
    @FXML private ListView<Equipment> lowStockList;
    @FXML private ListView<Equipment> expiryList;
    @FXML private ListView<Order> orderList;
    
    private InventoryService inventoryService;
    
    @FXML
    public void initialize() {
        inventoryService = new InventoryService();
        
        lowStockList.setCellFactory(param -> new EquipmentListCell());
        expiryList.setCellFactory(param -> new ExpiryEquipmentListCell());
        orderList.setCellFactory(param -> new OrderListCell());
        
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        List<Equipment> lowStockItems = inventoryService.getLowStockAlerts();
        System.out.println("Loading " + lowStockItems.size() + " low stock items");
        ObservableList<Equipment> lowStockData = FXCollections.observableArrayList(lowStockItems);
        lowStockList.setItems(lowStockData);
        lowStockCount.setText(String.valueOf(lowStockItems.size()));
        
        List<Equipment> expiryItems = inventoryService.getExpiryAlerts();
        System.out.println("Loading " + expiryItems.size() + " expiry alerts");
        ObservableList<Equipment> expiryData = FXCollections.observableArrayList(expiryItems);
        expiryList.setItems(expiryData);
        expiryAlertCount.setText(String.valueOf(expiryItems.size()));
        
        List<Order> activeOrders = inventoryService.getActiveOrders();
        System.out.println("Loading " + activeOrders.size() + " active orders");
        ObservableList<Order> orderData = FXCollections.observableArrayList(activeOrders);
        orderList.setItems(orderData);
        activeOrderCount.setText(String.valueOf(activeOrders.size()));
        
        System.out.println("Dashboard data loaded successfully!");
    }
    
    @FXML
    private void refreshDashboard() {
        loadDashboardData();
        System.out.println("Dashboard refreshed!");
    }
    
    @FXML
    private void openEquipmentManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/labinventory/equipment_management.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) lowStockList.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Equipment Management");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Equipment Management view: " + e.getMessage());
        }
    }
    
    private static class EquipmentListCell extends ListCell<Equipment> {
        @Override
        protected void updateItem(Equipment equipment, boolean empty) {
            super.updateItem(equipment, empty);
            if (empty || equipment == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox container = new HBox(15);
                container.setPadding(new Insets(12, 15, 12, 15));
                container.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1;");
                
                VBox nameBox = new VBox(3);
                Text name = new Text(equipment.getName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                Text category = new Text(equipment.getCategory());
                category.setStyle("-fx-font-size: 13px; -fx-fill: #7f8c8d;");
                nameBox.getChildren().addAll(name, category);
                HBox.setHgrow(nameBox, Priority.ALWAYS);
                
                VBox qtyBox = new VBox(3);
                qtyBox.setStyle("-fx-alignment: center-right;");
                Text qty = new Text(equipment.getQuantity() + " / " + equipment.getLowerLimit());
                qty.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FCA47C;");
                Text label = new Text("Current / Min");
                label.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                qtyBox.getChildren().addAll(qty, label);
                
                VBox locBox = new VBox(3);
                locBox.setStyle("-fx-alignment: center-right;");
                Text location = new Text(equipment.getLocation());
                location.setStyle("-fx-font-size: 13px; -fx-fill: #34495e;");
                Text locLabel = new Text("Location");
                locLabel.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                locBox.getChildren().addAll(locLabel, location);
                
                container.getChildren().addAll(nameBox, qtyBox, locBox);
                setGraphic(container);
            }
        }
    }
    
    private static class ExpiryEquipmentListCell extends ListCell<Equipment> {
        @Override
        protected void updateItem(Equipment equipment, boolean empty) {
            super.updateItem(equipment, empty);
            if (empty || equipment == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox container = new HBox(15);
                container.setPadding(new Insets(12, 15, 12, 15));
                container.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1;");
                
                VBox nameBox = new VBox(3);
                Text name = new Text(equipment.getName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                Text category = new Text(equipment.getCategory());
                category.setStyle("-fx-font-size: 13px; -fx-fill: #7f8c8d;");
                nameBox.getChildren().addAll(name, category);
                HBox.setHgrow(nameBox, Priority.ALWAYS);
                
                VBox dateBox = new VBox(3);
                dateBox.setStyle("-fx-alignment: center-right;");
                Text date = new Text(equipment.getExpiryDate().toString());
                date.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #F9D779;");
                Text label = new Text("Expires On");
                label.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                dateBox.getChildren().addAll(label, date);
                
                VBox daysBox = new VBox(3);
                daysBox.setStyle("-fx-alignment: center-right;");
                int daysLeft = equipment.getDaysUntilExpiry();
                Text days = new Text(daysLeft + " days");
                days.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: " + (daysLeft < 7 ? "#FCA47C" : "#F9D779") + ";");
                Text daysLabel = new Text("Days Left");
                daysLabel.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                daysBox.getChildren().addAll(daysLabel, days);
                
                container.getChildren().addAll(nameBox, dateBox, daysBox);
                setGraphic(container);
            }
        }
    }
    
    private static class OrderListCell extends ListCell<Order> {
        @Override
        protected void updateItem(Order order, boolean empty) {
            super.updateItem(order, empty);
            if (empty || order == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox container = new HBox(15);
                container.setPadding(new Insets(12, 15, 12, 15));
                String bgColor = order.getStatus().toString().equals("PENDING") ? "#E8F8FA" : "#EEF7EF";
                String borderColor = order.getStatus().toString().equals("PENDING") ? "#23CED9" : "#A1CCA6";
                container.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8; -fx-border-color: " + borderColor + "; -fx-border-radius: 8; -fx-border-width: 1;");
                
                VBox idBox = new VBox(3);
                Text id = new Text("Order #" + order.getId());
                id.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                idBox.getChildren().add(id);
                
                VBox nameBox = new VBox(3);
                Text name = new Text(order.getEquipmentName());
                name.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                Text qty = new Text("Qty: " + order.getQuantity());
                qty.setStyle("-fx-font-size: 13px; -fx-fill: #7f8c8d;");
                nameBox.getChildren().addAll(name, qty);
                HBox.setHgrow(nameBox, Priority.ALWAYS);
                
                VBox statusBox = new VBox(3);
                statusBox.setStyle("-fx-alignment: center;");
                Text status = new Text(order.getStatus().toString());
                status.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-fill: " + borderColor + ";");
                Text label = new Text("Status");
                label.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                statusBox.getChildren().addAll(label, status);
                
                VBox dateBox = new VBox(3);
                dateBox.setStyle("-fx-alignment: center-right;");
                Text date = new Text(order.getExpectedDeliveryDate().toString());
                date.setStyle("-fx-font-size: 14px; -fx-fill: #34495e;");
                Text dateLabel = new Text("Expected Delivery");
                dateLabel.setStyle("-fx-font-size: 11px; -fx-fill: #95a5a6;");
                dateBox.getChildren().addAll(dateLabel, date);
                
                container.getChildren().addAll(idBox, nameBox, statusBox, dateBox);
                setGraphic(container);
            }
        }
    }
}
