package com.labinventory.controller;

import com.labinventory.model.Equipment;
import com.labinventory.model.Order;
import com.labinventory.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipmentManagementController {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ListView<Equipment> equipmentList;
    
    private InventoryService inventoryService;
    
    @FXML
    public void initialize() {
        inventoryService = new InventoryService();
        
        sortComboBox.setItems(FXCollections.observableArrayList(
            "Name (A-Z)", "Name (Z-A)", 
            "Category", "Quantity (Low-High)", 
            "Quantity (High-Low)", "Price (Low-High)", 
            "Price (High-Low)", "Expiry Date"
        ));
        
        equipmentList.setCellFactory(lv -> new EquipmentListCell());
        
        loadEquipmentData();
    }
    
    @FXML
    public void loadEquipmentData() {
        List<Equipment> items = inventoryService.getAllEquipment();
        System.out.println("Loaded " + items.size() + " equipment items");
        ObservableList<Equipment> data = FXCollections.observableArrayList(items);
        equipmentList.setItems(data);
        
        if (items.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Data", 
                "No equipment found in database. The database may be empty.\n" +
                "Try deleting the lab_inventory.db file and restart the application.");
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadEquipmentData();
        } else {
            List<Equipment> results = inventoryService.searchEquipment(searchTerm);
            ObservableList<Equipment> data = FXCollections.observableArrayList(results);
            equipmentList.setItems(data);
        }
    }
    
    @FXML
    private void handleSort() {
        String sortOption = sortComboBox.getValue();
        if (sortOption == null) return;
        
        ObservableList<Equipment> items = equipmentList.getItems();
        List<Equipment> sortedList = new ArrayList<>(items);
        
        switch (sortOption) {
            case "Name (A-Z)":
                sortedList.sort((e1, e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
                break;
            case "Name (Z-A)":
                sortedList.sort((e1, e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
                break;
            case "Category":
                sortedList.sort((e1, e2) -> e1.getCategory().compareToIgnoreCase(e2.getCategory()));
                break;
            case "Quantity (Low-High)":
                sortedList.sort((e1, e2) -> Integer.compare(e1.getQuantity(), e2.getQuantity()));
                break;
            case "Quantity (High-Low)":
                sortedList.sort((e1, e2) -> Integer.compare(e2.getQuantity(), e1.getQuantity()));
                break;
            case "Price (Low-High)":
                sortedList.sort((e1, e2) -> Double.compare(e1.getUnitPrice(), e2.getUnitPrice()));
                break;
            case "Price (High-Low)":
                sortedList.sort((e1, e2) -> Double.compare(e2.getUnitPrice(), e1.getUnitPrice()));
                break;
            case "Expiry Date":
                sortedList.sort((e1, e2) -> {
                    if (e1.getExpiryDate() == null) return 1;
                    if (e2.getExpiryDate() == null) return -1;
                    return e1.getExpiryDate().compareTo(e2.getExpiryDate());
                });
                break;
        }
        
        equipmentList.setItems(FXCollections.observableArrayList(sortedList));
    }
    
    @FXML
    private void showAddDialog() {
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Add New Equipment");
        dialog.setHeaderText("Enter equipment details");
        
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        ButtonType addButtonType = new ButtonType("Add Equipment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        dialog.getDialogPane().lookupButton(addButtonType).setStyle(
            "-fx-background-color: linear-gradient(to right, #23CED9, #097C87); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-text-fill: #666; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        GridPane grid = createEquipmentForm(null);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return extractEquipmentFromForm(grid, null);
            }
            return null;
        });
        
        Optional<Equipment> result = dialog.showAndWait();
        result.ifPresent(equipment -> {
            if (inventoryService.addEquipment(equipment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment added successfully!");
                loadEquipmentData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add equipment.");
            }
        });
    }
    
    @FXML
    private void showEditDialog() {
        Equipment selected = equipmentList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an equipment to edit.");
            return;
        }
        
        Dialog<Equipment> dialog = new Dialog<>();
        dialog.setTitle("Edit Equipment");
        dialog.setHeaderText("Edit equipment details");
        
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        ButtonType updateButtonType = new ButtonType("Update Equipment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        dialog.getDialogPane().lookupButton(updateButtonType).setStyle(
            "-fx-background-color: linear-gradient(to right, #23CED9, #097C87); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-text-fill: #666; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        GridPane grid = createEquipmentForm(selected);
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return extractEquipmentFromForm(grid, selected);
            }
            return null;
        });
        
        Optional<Equipment> result = dialog.showAndWait();
        result.ifPresent(equipment -> {
            if (inventoryService.updateEquipment(equipment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment updated successfully!");
                loadEquipmentData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update equipment.");
            }
        });
    }
    
    @FXML
    private void deleteEquipment() {
        Equipment selected = equipmentList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an equipment to delete.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Equipment");
        confirmAlert.setContentText("Are you sure you want to delete '" + selected.getName() + "'?\\nThis action cannot be undone.");
        
        confirmAlert.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        confirmAlert.getDialogPane().lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #FCA47C; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20;"
        );
        confirmAlert.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-text-fill: #666; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20;"
        );
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (inventoryService.deleteEquipment(selected.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment deleted successfully!");
                loadEquipmentData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete equipment.");
            }
        }
    }
    
    @FXML
    private void showOrderDialog() {
        Equipment selected = equipmentList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an equipment to order.");
            return;
        }
        
        Dialog<Order> dialog = new Dialog<>();
        dialog.setTitle("Place Order");
        dialog.setHeaderText("Order: " + selected.getName());
        
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        ButtonType orderButtonType = new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(orderButtonType, ButtonType.CANCEL);
        
        dialog.getDialogPane().lookupButton(orderButtonType).setStyle(
            "-fx-background-color: linear-gradient(to right, #23CED9, #097C87); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-text-fill: #666; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 30, 25, 30));
        grid.setStyle("-fx-background-color: #E8F8FA; -fx-background-radius: 10;");
        
        TextField quantityField = createStyledTextField("", "Enter quantity to order");
        DatePicker deliveryDatePicker = new DatePicker();
        deliveryDatePicker.setValue(LocalDate.now().plusDays(10));
        deliveryDatePicker.setPromptText("Expected delivery date");
        deliveryDatePicker.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #A1CCA6; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 2; " +
            "-fx-pref-width: 250;"
        );
        TextField supplierField = createStyledTextField(selected.getSupplier(), "Supplier name");
        
        Label infoLabel = new Label("Current Price: \u20b9" + String.format("%.2f", selected.getUnitPrice()));
        infoLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #097C87; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 6; " +
            "-fx-border-color: #23CED9; " +
            "-fx-border-radius: 6; " +
            "-fx-border-width: 1;"
        );
        
        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(createStyledLabel("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(createStyledLabel("Expected Delivery:"), 0, 2);
        grid.add(deliveryDatePicker, 1, 2);
        grid.add(createStyledLabel("Supplier:"), 0, 3);
        grid.add(supplierField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == orderButtonType) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    Order order = new Order();
                    order.setEquipmentId(selected.getId());
                    order.setEquipmentName(selected.getName());
                    order.setQuantity(quantity);
                    order.setOrderDate(LocalDate.now());
                    order.setExpectedDeliveryDate(deliveryDatePicker.getValue());
                    order.setStatus(Order.OrderStatus.PENDING);
                    order.setSupplier(supplierField.getText());
                    order.setTotalCost(quantity * selected.getUnitPrice());
                    return order;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid quantity.");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Order> result = dialog.showAndWait();
        result.ifPresent(order -> {
            if (inventoryService.addOrder(order)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Order placed successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order.");
            }
        });
    }
    
    @FXML
    private void backToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/labinventory/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) equipmentList.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Lab Inventory Management System");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Home view: " + e.getMessage());
        }
    }
    
    private GridPane createEquipmentForm(Equipment equipment) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 30, 25, 30));
        grid.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
        
        TextField nameField = createStyledTextField(equipment != null ? equipment.getName() : "", "Enter equipment name");
        TextField categoryField = createStyledTextField(equipment != null ? equipment.getCategory() : "", "e.g., Lab Equipment");
        TextField quantityField = createStyledTextField(equipment != null ? String.valueOf(equipment.getQuantity()) : "", "Current quantity");
        TextField lowerLimitField = createStyledTextField(equipment != null ? String.valueOf(equipment.getLowerLimit()) : "", "Minimum stock level");
        TextField unitPriceField = createStyledTextField(equipment != null ? String.valueOf(equipment.getUnitPrice()) : "", "Price in ₹");
        DatePicker expiryDatePicker = new DatePicker(equipment != null ? equipment.getExpiryDate() : null);
        expiryDatePicker.setPromptText("Select expiry date");
        expiryDatePicker.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #A1CCA6; -fx-border-radius: 8; -fx-border-width: 2; -fx-pref-width: 200;");
        TextField locationField = createStyledTextField(equipment != null ? equipment.getLocation() : "", "Storage location");
        TextField supplierField = createStyledTextField(equipment != null ? equipment.getSupplier() : "", "Supplier name");
        
        grid.add(createStyledLabel("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(createStyledLabel("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(createStyledLabel("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(createStyledLabel("Min Stock:"), 0, 3);
        grid.add(lowerLimitField, 1, 3);
        grid.add(createStyledLabel("Price (₹):"), 0, 4);
        grid.add(unitPriceField, 1, 4);
        grid.add(createStyledLabel("Expiry Date:"), 0, 5);
        grid.add(expiryDatePicker, 1, 5);
        grid.add(createStyledLabel("Location:"), 0, 6);
        grid.add(locationField, 1, 6);
        grid.add(createStyledLabel("Supplier:"), 0, 7);
        grid.add(supplierField, 1, 7);
        
        return grid;
    }
    
    private TextField createStyledTextField(String text, String prompt) {
        TextField field = new TextField(text);
        field.setPromptText(prompt);
        field.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #A1CCA6; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 2; " +
            "-fx-padding: 10; " +
            "-fx-font-size: 13px; " +
            "-fx-pref-width: 250;"
        );
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #23CED9; " +
                    "-fx-border-radius: 8; " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 10; " +
                    "-fx-font-size: 13px; " +
                    "-fx-pref-width: 250; " +
                    "-fx-effect: dropshadow(gaussian, rgba(35, 206, 217, 0.4), 6, 0, 0, 0);"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #A1CCA6; " +
                    "-fx-border-radius: 8; " +
                    "-fx-border-width: 2; " +
                    "-fx-padding: 10; " +
                    "-fx-font-size: 13px; " +
                    "-fx-pref-width: 250;"
                );
            }
        });
        return field;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #097C87; " +
            "-fx-padding: 5;"
        );
        return label;
    }
    
    private Equipment extractEquipmentFromForm(GridPane grid, Equipment existingEquipment) {
        TextField nameField = (TextField) grid.getChildren().get(1);
        TextField categoryField = (TextField) grid.getChildren().get(3);
        TextField quantityField = (TextField) grid.getChildren().get(5);
        TextField lowerLimitField = (TextField) grid.getChildren().get(7);
        TextField unitPriceField = (TextField) grid.getChildren().get(9);
        DatePicker expiryDatePicker = (DatePicker) grid.getChildren().get(11);
        TextField locationField = (TextField) grid.getChildren().get(13);
        TextField supplierField = (TextField) grid.getChildren().get(15);
        
        Equipment equipment = existingEquipment != null ? existingEquipment : new Equipment();
        equipment.setName(nameField.getText());
        equipment.setCategory(categoryField.getText());
        equipment.setQuantity(Integer.parseInt(quantityField.getText()));
        equipment.setLowerLimit(Integer.parseInt(lowerLimitField.getText()));
        equipment.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
        equipment.setExpiryDate(expiryDatePicker.getValue());
        equipment.setLocation(locationField.getText());
        equipment.setSupplier(supplierField.getText());
        
        if (existingEquipment == null) {
            equipment.setDateAdded(LocalDate.now());
        }
        
        return equipment;
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        alert.getDialogPane().setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        
        String buttonColor = switch (type) {
            case ERROR -> "#FCA47C";
            case WARNING -> "#F9D779";
            case INFORMATION -> "#A1CCA6";
            default -> "#23CED9";
        };
        
        alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: " + buttonColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 20 10 20; " +
            "-fx-font-size: 13px;"
        );
        
        alert.showAndWait();
    }
    
    private static class EquipmentListCell extends ListCell<Equipment> {
        @Override
        protected void updateItem(Equipment equipment, boolean empty) {
            super.updateItem(equipment, empty);
            
            if (empty || equipment == null) {
                setGraphic(null);
                setText(null);
            } else {
                HBox card = new HBox(20);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; " +
                             "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                
                VBox leftSection = new VBox(5);
                leftSection.setPrefWidth(250);
                Text idText = new Text("ID: " + equipment.getId());
                idText.setStyle("-fx-font-size: 11px; -fx-fill: #666;");
                Text nameText = new Text(equipment.getName());
                nameText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #333;");
                Text categoryText = new Text(equipment.getCategory());
                categoryText.setStyle("-fx-font-size: 12px; -fx-fill: #097C87;");
                leftSection.getChildren().addAll(idText, nameText, categoryText);
                
                VBox middleSection = new VBox(5);
                middleSection.setPrefWidth(180);
                Text qtyText = new Text("Quantity: " + equipment.getQuantity());
                qtyText.setStyle("-fx-font-size: 12px; -fx-fill: #333;");
                Text minText = new Text("Min: " + equipment.getLowerLimit());
                minText.setStyle("-fx-font-size: 11px; -fx-fill: #666;");
                
                HBox stockStatus = new HBox(5);
                stockStatus.setAlignment(Pos.CENTER_LEFT);
                Text statusIcon = new Text("●");
                Text statusText;
                if (equipment.getQuantity() <= equipment.getLowerLimit()) {
                    statusIcon.setStyle("-fx-fill: #FCA47C; -fx-font-size: 16px;");
                    statusText = new Text("Low Stock");
                    statusText.setStyle("-fx-fill: #FCA47C; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else if (equipment.getQuantity() <= equipment.getLowerLimit() * 1.5) {
                    statusIcon.setStyle("-fx-fill: #F9D779; -fx-font-size: 16px;");
                    statusText = new Text("Moderate");
                    statusText.setStyle("-fx-fill: #F9D779; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    statusIcon.setStyle("-fx-fill: #A1CCA6; -fx-font-size: 16px;");
                    statusText = new Text("In Stock");
                    statusText.setStyle("-fx-fill: #A1CCA6; -fx-font-weight: bold; -fx-font-size: 11px;");
                }
                stockStatus.getChildren().addAll(statusIcon, statusText);
                
                middleSection.getChildren().addAll(qtyText, minText, stockStatus);
                
                VBox priceSection = new VBox(5);
                priceSection.setPrefWidth(150);
                Text priceText = new Text(String.format("₹%.2f", equipment.getUnitPrice()));
                priceText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #097C87;");
                Text locationText = new Text(equipment.getLocation());
                locationText.setStyle("-fx-font-size: 11px; -fx-fill: #666;");
                priceSection.getChildren().addAll(priceText, locationText);
                
                VBox rightSection = new VBox(5);
                rightSection.setPrefWidth(200);
                
                if (equipment.getExpiryDate() != null) {
                    long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(
                        LocalDate.now(), equipment.getExpiryDate());
                    Text expiryLabel = new Text("Expires:");
                    expiryLabel.setStyle("-fx-font-size: 10px; -fx-fill: #888;");
                    Text expiryText = new Text(equipment.getExpiryDate().toString());
                    if (daysLeft <= 15) {
                        expiryText.setStyle("-fx-font-size: 11px; -fx-fill: #FCA47C; -fx-font-weight: bold;");
                    } else {
                        expiryText.setStyle("-fx-font-size: 11px; -fx-fill: #666;");
                    }
                    rightSection.getChildren().addAll(expiryLabel, expiryText);
                } else {
                    Text noExpiry = new Text("No expiry date");
                    noExpiry.setStyle("-fx-font-size: 11px; -fx-fill: #999;");
                    rightSection.getChildren().add(noExpiry);
                }
                
                Text supplierText = new Text("Supplier: " + equipment.getSupplier());
                supplierText.setStyle("-fx-font-size: 10px; -fx-fill: #888;");
                rightSection.getChildren().add(supplierText);
                
                card.getChildren().addAll(leftSection, middleSection, priceSection, rightSection);
                
                card.setOnMouseEntered(e -> card.setStyle(
                    "-fx-background-color: #E8F8FA; -fx-padding: 15; -fx-background-radius: 8; " +
                    "-fx-border-color: #23CED9; -fx-border-radius: 8; -fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(35, 206, 217, 0.4), 8, 0, 0, 3); -fx-cursor: hand;"));
                
                card.setOnMouseExited(e -> card.setStyle(
                    "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"));
                
                setGraphic(card);
            }
        }
    }
}
