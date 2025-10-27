package com.vehiclemonitoring.auth.util;

import java.sql.*;
import java.util.*;

/**
 * Utilidad para inspeccionar la estructura de la base de datos PostgreSQL
 */
public class DatabaseInspector {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/car_monitoring_auth";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";
    private static final String SCHEMA = "auth";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            
            try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                System.out.println("Conectado exitosamente a la base de datos!");
                
                // Listar todas las tablas en el schema auth
                listTables(connection);
                
                // Obtener estructura de cada tabla
                List<String> tables = getTableNames(connection);
                for (String table : tables) {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("TABLA: " + table);
                    System.out.println("=".repeat(50));
                    describeTable(connection, table);
                }
                
            }
        } catch (Exception e) {
            System.err.println("Error conectando a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void listTables(Connection connection) throws SQLException {
        System.out.println("\n TABLAS EN EL SCHEMA 'auth':");
        System.out.println("-".repeat(30));
        
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, SCHEMA);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                System.out.println("• " + rs.getString("table_name"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("No se encontraron tablas en el schema 'auth'");
            }
        }
    }
    
    private static List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tables = new ArrayList<>();
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, SCHEMA);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tables.add(rs.getString("table_name"));
            }
        }
        return tables;
    }
    
    private static void describeTable(Connection connection, String tableName) throws SQLException {
        String query = """
            SELECT 
                column_name,
                data_type,
                is_nullable,
                column_default,
                character_maximum_length,
                numeric_precision,
                numeric_scale
            FROM information_schema.columns 
            WHERE table_schema = ? AND table_name = ?
            ORDER BY ordinal_position
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, SCHEMA);
            stmt.setString(2, tableName);
            ResultSet rs = stmt.executeQuery();
            
            System.out.printf("%-20s %-15s %-10s %-20s %-10s%n", 
                            "COLUMNA", "TIPO", "NULLABLE", "DEFAULT", "LONGITUD");
            System.out.println("-".repeat(80));
            
            while (rs.next()) {
                String columnName = rs.getString("column_name");
                String dataType = rs.getString("data_type");
                String isNullable = rs.getString("is_nullable");
                String columnDefault = rs.getString("column_default");
                Integer maxLength = rs.getObject("character_maximum_length", Integer.class);
                
                System.out.printf("%-20s %-15s %-10s %-20s %-10s%n",
                    columnName,
                    dataType,
                    isNullable,
                    columnDefault != null ? columnDefault : "N/A",
                    maxLength != null ? maxLength.toString() : "N/A"
                );
            }
        }
        
        // Mostrar claves primarias
        showPrimaryKeys(connection, tableName);
        
        // Mostrar claves foráneas
        showForeignKeys(connection, tableName);
    }
    
    private static void showPrimaryKeys(Connection connection, String tableName) throws SQLException {
        String query = """
            SELECT column_name
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name = kcu.constraint_name
            WHERE tc.constraint_type = 'PRIMARY KEY'
            AND tc.table_schema = ?
            AND tc.table_name = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, SCHEMA);
            stmt.setString(2, tableName);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("\n🔑 CLAVES PRIMARIAS:");
            while (rs.next()) {
                System.out.println("• " + rs.getString("column_name"));
            }
        }
    }
    
    private static void showForeignKeys(Connection connection, String tableName) throws SQLException {
        String query = """
            SELECT 
                kcu.column_name,
                ccu.table_name AS foreign_table_name,
                ccu.column_name AS foreign_column_name
            FROM information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
            ON tc.constraint_name = kcu.constraint_name
            JOIN information_schema.constraint_column_usage AS ccu
            ON ccu.constraint_name = tc.constraint_name
            WHERE tc.constraint_type = 'FOREIGN KEY'
            AND tc.table_schema = ?
            AND tc.table_name = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, SCHEMA);
            stmt.setString(2, tableName);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("\n🔗 CLAVES FORÁNEAS:");
            while (rs.next()) {
                System.out.printf("• %s -> %s.%s%n",
                    rs.getString("column_name"),
                    rs.getString("foreign_table_name"),
                    rs.getString("foreign_column_name")
                );
            }
        }
    }
}