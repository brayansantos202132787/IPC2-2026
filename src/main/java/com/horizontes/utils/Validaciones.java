/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.utils;

import com.horizontes.exceptions.ValidationException;

/**
 *
 * @author braya
 */
public class Validaciones {
    
     private Validaciones() {}
 
    
 
    public static void requerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidationException("El campo '" + campo + "' es requerido.");
        }
    }
 
    public static void longitud(String valor, String campo, int max) {
        if (valor != null && valor.length() > max) {
            throw new ValidationException(
                    "El campo '" + campo + "' excede el máximo de " + max + " caracteres.");
        }
    }
 
    public static void longitudMinima(String valor, String campo, int min) {
        if (valor == null || valor.length() < min) {
            throw new ValidationException(
                    "El campo '" + campo + "' debe tener al menos " + min + " caracteres.");
        }
    }
 
    
 
    public static void positivo(Number valor, String campo) {
        if (valor == null || valor.doubleValue() <= 0) {
            throw new ValidationException("El campo '" + campo + "' debe ser un número positivo.");
        }
    }
 
    public static void noNegativo(Number valor, String campo) {
        if (valor == null || valor.doubleValue() < 0) {
            throw new ValidationException("El campo '" + campo + "' no puede ser negativo.");
        }
    }
 
    public static void enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new ValidationException("El campo '" + campo + "' debe ser un entero positivo.");
        }
    }
 
    
    public static void noNulo(Object valor, String campo) {
        if (valor == null) {
            throw new ValidationException("El campo '" + campo + "' es requerido.");
        }
    }
 
   
    public static void password(String password) {
        requerido(password, "password");
        longitudMinima(password, "password", 6);
    }
 
    
    public static void dpi(String dpi) {
        requerido(dpi, "dpi");
        longitud(dpi, "dpi", 20);
    }
 
    
    public static void fechaViajeNoEnPasado(java.time.LocalDate fechaViaje) {
        if (fechaViaje.isBefore(java.time.LocalDate.now())) {
            throw new ValidationException("La fecha de viaje no puede ser en el pasado.");
        }
    }
}
