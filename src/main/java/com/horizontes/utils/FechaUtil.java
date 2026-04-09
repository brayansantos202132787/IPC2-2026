/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author braya
 */
public class FechaUtil {
    /** Formato estándar de archivo de carga: dd/MM/yyyy */
    public static final DateTimeFormatter FMT_ARCHIVO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
 
    /** Formato ISO para JSON: yyyy-MM-dd */
    public static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ISO_LOCAL_DATE;
 
    private FechaUtil() {}
 
    /** Convierte java.sql.Date → LocalDate. */
    public static LocalDate toLocal(Date sqlDate) {
        return sqlDate == null ? null : sqlDate.toLocalDate();
    }
 
    /** Convierte LocalDate → java.sql.Date. */
    public static Date toSql(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
 
    /**
     * Parsea una cadena con formato dd/MM/yyyy (usado en el archivo de carga).
     * @throws IllegalArgumentException si el formato es inválido.
     */
    public static LocalDate parsearArchivoFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FMT_ARCHIVO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Fecha con formato incorrecto '" + texto + "'. Se esperaba dd/MM/yyyy.");
        }
    }
 
    /**
     * Parsea una cadena ISO (yyyy-MM-dd), usada en requests JSON.
     * @throws IllegalArgumentException si el formato es inválido.
     */
    public static LocalDate parsearIso(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FMT_ISO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Fecha con formato incorrecto '" + texto + "'. Se esperaba yyyy-MM-dd.");
        }
    }
 
    /** Calcula los días de diferencia entre dos fechas (fecha2 - fecha1). */
    public static long diasEntre(LocalDate fecha1, LocalDate fecha2) {
        return ChronoUnit.DAYS.between(fecha1, fecha2);
    }
 
    /** Retorna la fecha de hoy. */
    public static LocalDate hoy() {
        return LocalDate.now();
    }
 
  
    public static String formatearIso(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FMT_ISO);
    }
}
