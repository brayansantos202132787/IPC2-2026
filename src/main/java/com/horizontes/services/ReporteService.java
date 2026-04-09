/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.dao.ReporteDAO;
import com.horizontes.dto.ReporteDTO.*;
import com.horizontes.exceptions.ApiException;
import com.horizontes.utils.FechaUtil;
import jakarta.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
 

/**
 *
 * @author braya
 */
public class ReporteService {
    
     private final ReporteDAO reporteDAO = new ReporteDAO();
 
    
    public List<FilaVenta> reporteVentas(String desde, String hasta) {
        try {
            return reporteDAO.reporteVentas(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de ventas: " + e.getMessage(), e);
        }
    }
 
    // =================== CANCELACIONES ===================
    public List<FilaCancelacion> reporteCancelaciones(String desde, String hasta) {
        try {
            return reporteDAO.reporteCancelaciones(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de cancelaciones: " + e.getMessage(), e);
        }
    }
 
    // =================== GANANCIAS ===================
    public ResumenGanancias reporteGanancias(String desde, String hasta) {
        try {
            return reporteDAO.reporteGanancias(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de ganancias: " + e.getMessage(), e);
        }
    }
 
    // =================== AGENTE MÁS VENTAS ===================
    public AgenteReporte agenteMasVentas(String desde, String hasta) {
        try {
            return reporteDAO.agenteMasVentas(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de agente: " + e.getMessage(), e);
        }
    }
 
    // =================== AGENTE MÁS GANANCIAS ===================
    public AgenteReporte agenteMasGanancias(String desde, String hasta) {
        try {
            return reporteDAO.agenteMasGanancias(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de agente: " + e.getMessage(), e);
        }
    }
 
    // =================== PAQUETE MÁS/MENOS VENDIDO ===================
    public PaqueteReporte paqueteMasVendido(String desde, String hasta) {
        try {
            return reporteDAO.paqueteMasVendido(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de paquete: " + e.getMessage(), e);
        }
    }
 
    public PaqueteReporte paqueteMenosVendido(String desde, String hasta) {
        try {
            return reporteDAO.paqueteMenosVendido(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de paquete: " + e.getMessage(), e);
        }
    }
 
    // =================== OCUPACIÓN POR DESTINO ===================
    public List<OcupacionDestino> reporteOcupacion(String desde, String hasta) {
        try {
            return reporteDAO.reporteOcupacionDestino(parsear(desde), parsear(hasta));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al generar reporte de ocupación: " + e.getMessage(), e);
        }
    }
 
    // =================== EXPORTAR CSV ===================
 
    public void exportarVentasCsv(HttpServletResponse resp, String desde, String hasta) throws IOException {
        List<FilaVenta> datos = reporteVentas(desde, hasta);
        prepararRespuestaCsv(resp, "reporte_ventas.csv");
        try (PrintWriter w = resp.getWriter()) {
            w.println("N° Reservacion,Paquete,Agente,Fecha Viaje,Pasajeros,Costo Total,Listado Pasajeros");
            for (FilaVenta f : datos) {
                w.printf("%s,%s,%s,%s,%d,%.2f,%s%n",
                    csv(f.numeroReservacion), csv(f.paquete), csv(f.agente),
                    f.fechaViaje, f.cantidadPasajeros, f.costoTotal, csv(f.pasajeros));
            }
        }
    }
 
    public void exportarCancelacionesCsv(HttpServletResponse resp, String desde, String hasta) throws IOException {
        List<FilaCancelacion> datos = reporteCancelaciones(desde, hasta);
        prepararRespuestaCsv(resp, "reporte_cancelaciones.csv");
        try (PrintWriter w = resp.getWriter()) {
            w.println("N° Reservacion,Paquete,Fecha Cancelacion,Porcentaje Reembolso,Monto Reembolsado,Perdida Agencia");
            for (FilaCancelacion f : datos) {
                w.printf("%s,%s,%s,%.2f,%.2f,%.2f%n",
                    csv(f.numeroReservacion), csv(f.paquete), f.fechaCancelacion,
                    f.porcentajeReembolso, f.montoReembolsado, f.perdidaAgencia);
            }
        }
    }
 
    public void exportarGananciasCsv(HttpServletResponse resp, String desde, String hasta) throws IOException {
        ResumenGanancias g = reporteGanancias(desde, hasta);
        prepararRespuestaCsv(resp, "reporte_ganancias.csv");
        try (PrintWriter w = resp.getWriter()) {
            w.println("Concepto,Monto");
            w.printf("Ganancia Bruta,%.2f%n",  g.gananciaBruta);
            w.printf("Total Reembolsos,%.2f%n", g.totalReembolsos);
            w.printf("Ganancia Neta,%.2f%n",    g.gananciaNeta);
        }
    }
 
    public void exportarOcupacionCsv(HttpServletResponse resp, String desde, String hasta) throws IOException {
        List<OcupacionDestino> datos = reporteOcupacion(desde, hasta);
        prepararRespuestaCsv(resp, "reporte_ocupacion.csv");
        try (PrintWriter w = resp.getWriter()) {
            w.println("Destino,Total Reservaciones,Total Pasajeros");
            for (OcupacionDestino od : datos) {
                w.printf("%s,%d,%d%n", csv(od.destino), od.totalReservaciones, od.totalPasajeros);
            }
        }
    }
 
    // =================== AUXILIARES ===================
    private LocalDate parsear(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        return FechaUtil.parsearIso(fecha);
    }
 
    private void prepararRespuestaCsv(HttpServletResponse resp, String filename) {
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
 
    /** Escapa comillas y envuelve en comillas si contiene coma. */
    private String csv(String valor) {
        if (valor == null) return "";
        String escapado = valor.replace("\"", "\"\"");
        if (escapado.contains(",") || escapado.contains("\n")) {
            return "\"" + escapado + "\"";
        }
        return escapado;
    }
}
