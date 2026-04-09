/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author braya
 */
public class ReservacionDTO {
    public int           idPaquete;
    public String        fechaViaje;       // yyyy-MM-dd
    public List<String>  dpisPasajeros;    // lista de DPI/pasaportes
 
    // -------- RESPONSE (resumen confirmación) --------
    public String        numeroReservacion;
    public String        nombrePaquete;
    public String        nombreDestino;
    public int           duracionDias;
    public LocalDate     fechaViajeDate;
    public int           cantidadPasajeros;
    public BigDecimal    costoTotal;
    public String        estado;
    public String        agente;
    public LocalDate     fechaCreacion;
    public List<String>  nombresPasajeros;
 
    public ReservacionDTO() {}
}
