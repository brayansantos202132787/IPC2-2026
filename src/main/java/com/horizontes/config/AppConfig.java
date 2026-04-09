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
public class AppConfig {
      private AppConfig() {}
 
   
    public static final String DB_HOST     = "localhost";
    public static final String DB_PORT     = "3306";
    public static final String DB_NAME     = "horizontes_sin_limites";
    public static final String DB_USER     = "root";
    public static final String DB_PASSWORD = "root";
    public static final String DB_URL      =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=America/Guatemala&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
 
    
    public static final String CORS_ORIGIN = "http://localhost:8080";
 
    
    public static final int SESSION_TIMEOUT_SECONDS = 3600; 
    public static final int ROL_ATENCION_CLIENTE = 1;
    public static final int ROL_OPERACIONES      = 2;
    public static final int ROL_ADMINISTRADOR    = 3;
 
    
    public static final int DEFAULT_PAGE_SIZE = 20;
 
   
    public static final int CANCEL_DIAS_MIN          = 7;
    
    public static final int CANCEL_DIAS_100          = 30;
    
    public static final int CANCEL_DIAS_70_MIN       = 15;
    
    public static final int CANCEL_DIAS_40_MIN       = 7;
 
    public static final double CANCEL_PORCENTAJE_100 = 100.0;
    public static final double CANCEL_PORCENTAJE_70  = 70.0;
    public static final double CANCEL_PORCENTAJE_40  = 40.0;
 
   
    public static final double ALERTA_OCUPACION_PORCIENTO = 0.80;
 
    
    public static final String RESERVACION_PREFIX = "RES-";
}

