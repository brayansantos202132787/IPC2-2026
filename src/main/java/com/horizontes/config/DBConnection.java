/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author braya
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
 
    private static final String URL      = AppConfig.DB_URL;
    private static final String USER     = AppConfig.DB_USER;
    private static final String PASSWORD = AppConfig.DB_PASSWORD;
 
    private DBConnection() {
        connect();
    }
 
    /** Devuelve la instancia única (Singleton). */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
 
    /**
     * Devuelve una conexión activa.
     * Si la conexión está cerrada o es nula, reconecta automáticamente.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }
 
    /** Abre la conexión JDBC. */
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Conexión establecida con la base de datos.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[DB] Driver MySQL no encontrado: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("[DB] Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }
 
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
