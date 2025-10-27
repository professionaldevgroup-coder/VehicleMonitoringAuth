package com.vehiclemonitoring.auth.util;

import java.sql.*;
import java.util.Properties;

/**
 * Utilidad para diagnosticar problemas de conexión con PostgreSQL
 */
public class ConnectionDiagnostic {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/car_monitoring_auth";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";
    
    public static void main(String[] args) {
        System.out.println("🔍 DIAGNÓSTICO DE CONEXIÓN POSTGRESQL");
        System.out.println("=" + "=".repeat(40));
        
        // Test 1: Verificar driver
        testDriverAvailability();
        
        // Test 2: Conexión básica
        testBasicConnection();
        
        // Test 3: Conexión con propiedades adicionales
        testConnectionWithProperties();
        
        // Test 4: Verificar la base de datos específica
        testDatabaseExists();
        
        // Test 5: Verificar el schema
        testSchemaExists();
        
        // Test 6: Configuración de pool de conexiones
        testConnectionPool();
    }
    
    private static void testDriverAvailability() {
        System.out.println("\nTest 1: Verificando driver PostgreSQL...");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL disponible");
            
            Driver driver = DriverManager.getDriver(URL);
            System.out.println("Driver registrado: " + driver.getClass().getName());
            System.out.println("   Versión: " + driver.getMajorVersion() + "." + driver.getMinorVersion());
        } catch (Exception e) {
            System.err.println("Error con driver: " + e.getMessage());
        }
    }
    
    private static void testBasicConnection() {
        System.out.println("\n Test 2: Conexión básica...");
        System.out.println("URL: " + URL);
        System.out.println("Usuario: " + USERNAME);
        
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Conexión básica exitosa");
            System.out.println("   Producto: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("   Versión: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("   AutoCommit: " + connection.getAutoCommit());
            System.out.println("   Catálogo: " + connection.getCatalog());
            System.out.println("   Schema: " + connection.getSchema());
        } catch (SQLException e) {
            System.err.println("Error en conexión básica:");
            System.err.println("   Código: " + e.getErrorCode());
            System.err.println("   Estado SQL: " + e.getSQLState());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testConnectionWithProperties() {
        System.out.println("\nTest 3: Conexión con propiedades Spring Boot...");
        
        Properties props = new Properties();
        props.setProperty("user", USERNAME);
        props.setProperty("password", PASSWORD);
        props.setProperty("ApplicationName", "VehicleMonitoringAuth");
        props.setProperty("connectTimeout", "10");
        props.setProperty("socketTimeout", "0");
        props.setProperty("tcpKeepAlive", "true");
        
        try (Connection connection = DriverManager.getConnection(URL, props)) {
            System.out.println("Conexión con propiedades exitosa");
            
            // Probar una consulta simple
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT version()")) {
                if (rs.next()) {
                    System.out.println("   Versión PostgreSQL: " + rs.getString(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en conexión con propiedades: " + e.getMessage());
        }
    }
    
    private static void testDatabaseExists() {
        System.out.println("\nTest 4: Verificando base de datos...");
        
        String serverUrl = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection connection = DriverManager.getConnection(serverUrl, USERNAME, PASSWORD)) {
            String query = "SELECT 1 FROM pg_database WHERE datname = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "car_monitoring_auth");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    System.out.println("Base de datos 'car_monitoring_auth' existe");
                } else {
                    System.out.println("Base de datos 'car_monitoring_auth' NO existe");
                    
                    // Listar bases de datos disponibles
                    System.out.println("📋 Bases de datos disponibles:");
                    try (Statement listStmt = connection.createStatement();
                         ResultSet listRs = listStmt.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
                        while (listRs.next()) {
                            System.out.println("   • " + listRs.getString("datname"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verificando base de datos: " + e.getMessage());
        }
    }
    
    private static void testSchemaExists() {
        System.out.println("\nTest 5: Verificando schema 'auth'...");
        
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT 1 FROM information_schema.schemata WHERE schema_name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "auth");
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    System.out.println("✅ Schema 'auth' existe");
                    
                    // Contar tablas en el schema
                    String countQuery = "SELECT COUNT(*) as count FROM information_schema.tables WHERE table_schema = ?";
                    try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
                        countStmt.setString(1, "auth");
                        ResultSet countRs = countStmt.executeQuery();
                        if (countRs.next()) {
                            System.out.println("   Tablas encontradas: " + countRs.getInt("count"));
                        }
                    }
                } else {
                    System.out.println("Schema 'auth' NO existe");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verificando schema: " + e.getMessage());
        }
    }
    
    private static void testConnectionPool() {
        System.out.println("\nTest 6: Simulando pool de conexiones...");
        
        try {
            // Crear múltiples conexiones como lo haría Hikari
            for (int i = 1; i <= 3; i++) {
                try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
                    System.out.println("Conexión #" + i + " exitosa");
                    
                    // Simular uso de la conexión
                    try (Statement stmt = connection.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT current_timestamp")) {
                        if (rs.next()) {
                            System.out.println("   Timestamp: " + rs.getTimestamp(1));
                        }
                    }
                    
                    // Pequeña pausa
                    Thread.sleep(100);
                } catch (SQLException e) {
                    System.err.println("Error en conexión #" + i + ": " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(" Error en test de pool: " + e.getMessage());
        }
        
        System.out.println("\n Diagnóstico completo");
    }
}