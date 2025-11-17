package com.labinventory.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_NAME = "lab_inventory.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static Connection connection = null;

    public static void initialize() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established.");
            createTables();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Error getting database connection: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    private static void createTables() {
        String createEquipmentsTable = """
            CREATE TABLE IF NOT EXISTS equipments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                lower_limit INTEGER NOT NULL,
                unit_price REAL NOT NULL,
                expiry_date DATE,
                location TEXT,
                supplier TEXT,
                date_added DATE DEFAULT CURRENT_DATE
            )
        """;

        String createOrdersTable = """
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                equipment_id INTEGER NOT NULL,
                equipment_name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                order_date DATE DEFAULT CURRENT_DATE,
                expected_delivery_date DATE,
                actual_delivery_date DATE,
                status TEXT NOT NULL CHECK(status IN ('PENDING', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')),
                supplier TEXT NOT NULL,
                total_cost REAL,
                FOREIGN KEY (equipment_id) REFERENCES equipments(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createEquipmentsTable);
            stmt.execute(createOrdersTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isDatabaseEmpty() {
        String query = "SELECT COUNT(*) as count FROM equipments";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking database: " + e.getMessage());
        }
        return true;
    }
}
