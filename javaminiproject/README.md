# Lab Inventory Management System

A JavaFX-based desktop application for managing laboratory equipment inventory with features for tracking stock levels, expiry dates, and orders.

## Features

### 🏠 Homepage Dashboard
- **Real-time Statistics**: Display counts for low stock alerts, expiry alerts, and active orders
- **Low Stock Alerts**: Automatic detection when equipment quantity ≤ lower limit
- **Expiry Date Alerts**: Notifications for items expiring within 15 days
- **Order Tracking**: Monitor active orders (PENDING and IN_TRANSIT status)

### 🛠️ Equipment Management
- **CRUD Operations**: Create, Read, Update, Delete equipment
- **Search Functionality**: Search by equipment name or category
- **Equipment Details**: Track name, category, quantity, price, expiry date, location, supplier
- **Order Placement**: Directly order equipment from the management interface

## Technology Stack

- **Frontend**: JavaFX 17
- **Backend**: Java 11
- **Database**: SQLite (embedded)
- **Build Tool**: Maven

## Project Structure

```
javaminiproject/
├── src/
│   ├── main/
│   │   ├── java/com/labinventory/
│   │   │   ├── LabInventoryApp.java          # Main application entry point
│   │   │   ├── controller/
│   │   │   │   ├── HomeController.java        # Home dashboard controller
│   │   │   │   └── EquipmentManagementController.java
│   │   │   ├── database/
│   │   │   │   ├── DatabaseManager.java       # Database connection manager
│   │   │   │   ├── EquipmentDAO.java          # Equipment data access
│   │   │   │   └── OrderDAO.java              # Order data access
│   │   │   ├── model/
│   │   │   │   ├── Equipment.java             # Equipment model
│   │   │   │   └── Order.java                 # Order model
│   │   │   └── service/
│   │   │       └── InventoryService.java      # Business logic layer
│   │   └── resources/com/labinventory/
│   │       ├── home.fxml                      # Home view
│   │       ├── equipment_management.fxml      # Equipment management view
│   │       └── styles.css                     # Application styles
├── java.sql                                   # Database schema and sample data
└── pom.xml                                    # Maven configuration
```

## How It Works

### Low Stock Alert Implementation
```java
// In Equipment.java
public boolean isLowStock() {
    return quantity <= lowerLimit;
}

// In EquipmentDAO.java
public List<Equipment> getLowStockEquipment() {
    String query = "SELECT * FROM equipments WHERE quantity <= lower_limit";
    // Returns all equipment where current quantity is at or below the minimum threshold
}
```

### Expiry Date Alert Implementation
```java
// In Equipment.java
public boolean isNearExpiry() {
    if (expiryDate == null) return false;
    LocalDate fifteenDaysFromNow = LocalDate.now().plusDays(15);
    return expiryDate.isBefore(fifteenDaysFromNow) || expiryDate.isEqual(fifteenDaysFromNow);
}

// In EquipmentDAO.java
public List<Equipment> getEquipmentNearExpiry() {
    LocalDate fifteenDaysFromNow = LocalDate.now().plusDays(15);
    String query = "SELECT * FROM equipments WHERE expiry_date IS NOT NULL 
                    AND expiry_date <= ?";
    // Returns equipment expiring within 15 days
}
```

### Order Tracking Implementation
```java
// In Order.java - OrderStatus enum
public enum OrderStatus {
    PENDING,      // Order placed, awaiting processing
    IN_TRANSIT,   // Order shipped, on the way
    DELIVERED,    // Order received
    CANCELLED     // Order cancelled
}

// In OrderDAO.java
public List<Order> getActiveOrders() {
    String query = "SELECT * FROM orders WHERE status IN ('PENDING', 'IN_TRANSIT')";
    // Returns only orders that are being actively tracked
}
```

## Installation & Running

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Steps to Run

1. **Navigate to project directory**:
   ```powershell
   cd c:\Users\nialr\.vscode\javaminiproject
   ```

2. **Clean and build the project**:
   ```powershell
   mvn clean install
   ```

3. **Run the application**:
   ```powershell
   mvn javafx:run
   ```

   Alternatively, you can run directly:
   ```powershell
   java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -jar target/lab-inventory-system-1.0-SNAPSHOT.jar
   ```

## Database Schema

### Equipments Table
- `id`: Primary key
- `name`: Equipment name
- `category`: Equipment category
- `quantity`: Current stock quantity
- `lower_limit`: Minimum required quantity (for low stock alerts)
- `unit_price`: Price per unit
- `expiry_date`: Expiration date (nullable)
- `location`: Storage location
- `supplier`: Supplier name
- `date_added`: Date when equipment was added

### Orders Table
- `id`: Primary key
- `equipment_id`: Foreign key to equipments
- `equipment_name`: Name of ordered equipment
- `quantity`: Order quantity
- `order_date`: Date order was placed
- `expected_delivery_date`: Expected delivery date
- `actual_delivery_date`: Actual delivery date (nullable)
- `status`: Order status (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)
- `supplier`: Supplier name
- `total_cost`: Total order cost

## Usage Guide

### Dashboard
- View real-time alerts and statistics
- Monitor low stock items
- Check expiring equipment
- Track active orders
- Click "Equipment Management" to manage inventory

### Equipment Management
- **Add**: Click "+ Add Equipment" button
- **Edit**: Select equipment, click "Edit Selected"
- **Delete**: Select equipment, click "Delete Selected"
- **Search**: Type in search box to filter by name/category
- **Order**: Select equipment, click "Place Order"

## Sample Data
The application comes pre-loaded with 30 sample equipment items and 7 sample orders covering various categories:
- Optical Equipment
- Lab Tools
- Glassware
- Chemicals
- Safety Equipment
- Medical Supplies
- And more...

## Notes
- Database file (`lab_inventory.db`) is automatically created on first run
- Sample data is loaded automatically if database is empty
- All dates use ISO format (YYYY-MM-DD)
- Prices are in USD

## Future Enhancements
- Export reports to PDF/Excel
- User authentication and roles
- Email notifications for alerts
- Barcode/QR code integration
- Mobile app companion
- Advanced analytics and charts

---

**Author**: Lab Inventory Team  
**Version**: 1.0-SNAPSHOT  
**License**: MIT
