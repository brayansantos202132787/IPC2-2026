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
public class ReporteDTO {
    private ReporteDTO() {}
 
    // =================== VENTAS ===================
    public static class FilaVenta {
        public String     numeroReservacion;
        public String     paquete;
        public String     agente;
        public LocalDate  fechaViaje;
        public int        cantidadPasajeros;
        public BigDecimal costoTotal;
        public String     pasajeros;   // nombres separados por coma
    }
 
    // =================== CANCELACIONES ===================
    public static class FilaCancelacion {
        public String     numeroReservacion;
        public String     paquete;
        public LocalDate  fechaCancelacion;
        public BigDecimal montoReembolsado;
        public BigDecimal porcentajeReembolso;
        public BigDecimal perdidaAgencia;
    }
 
    // =================== GANANCIAS ===================
    public static class ResumenGanancias {
        public LocalDate  desde;
        public LocalDate  hasta;
        public BigDecimal gananciaBruta;
        public BigDecimal totalReembolsos;
        public BigDecimal gananciaNeta;
    }
 
    // =================== AGENTE ===================
    public static class AgenteReporte {
        public String          nombre;
        public int             totalReservaciones;
        public BigDecimal      montoTotal;
        public List<FilaVenta> reservaciones;  // detalle
    }
 
    // =================== PAQUETE ===================
    public static class PaqueteReporte {
        public String          nombre;
        public int             totalReservaciones;
        public List<FilaVenta> reservaciones;
    }
 
    // =================== OCUPACIÓN POR DESTINO ===================
    public static class OcupacionDestino {
        public String destino;
        public int    totalReservaciones;
        public int    totalPasajeros;
    }
}
