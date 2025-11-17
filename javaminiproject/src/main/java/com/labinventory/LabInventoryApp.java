package com.labinventory;

import com.labinventory.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

public class LabInventoryApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager.initialize();
            
            boolean isEmpty = DatabaseManager.isDatabaseEmpty();
            System.out.println("Database is empty: " + isEmpty);
            if (isEmpty) {
                populateSampleData();
            } else {
                System.out.println("Database already contains data - skipping sample data load");
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/labinventory/home.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setTitle("Lab Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                DatabaseManager.close();
                System.out.println("Application closed.");
            });
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }
    
    private void populateSampleData() {
        System.out.println("Populating database with sample data...");
        
        try {
            String sqlFile = "java2.sql";
            Connection conn = DatabaseManager.getConnection();
            Statement stmt = conn.createStatement();
            
            StringBuilder sqlContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(sqlFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
                        sqlContent.append(line).append("\n");
                    }
                }
            }
            
            String[] sqlStatements = sqlContent.toString().split(";");
            for (String sql : sqlStatements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    try {
                        stmt.execute(trimmedSql);
                    } catch (Exception e) {
                        System.out.println("Note: " + e.getMessage());
                    }
                }
            }
            
            stmt.close();
            System.out.println("Sample data loaded successfully!");
            
        } catch (IOException e) {
            System.err.println("Error reading SQL file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error populating sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
