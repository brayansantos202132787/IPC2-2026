/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.dto.ReporteDTO.*;
import com.horizontes.utils.FechaUtil;
 
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ReporteDAO {
    
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    
    public List<FilaVenta> reporteVentas(LocalDate desde, LocalDate hasta) throws SQLException {
        List<FilaVenta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.numero_reservacion, p.nombre AS paquete, u.nombre AS agente, " +
                "r.fecha_viaje, r.cantidad_pasajeros, r.costo_total, " +
                "GROUP_CONCAT(c.nombre ORDER BY c.nombre SEPARATOR ', ') AS pasajeros " +
                "FROM reservacion r " +
                "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                "LEFT JOIN reservacion_pasajero rp ON r.id_reservacion = rp.id_reservacion " +
                "LEFT JOIN cliente c ON rp.id_cliente = c.id_cliente " +
                "WHERE r.id_estado = 2 ");  // Confirmada
 
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
        sql.append("GROUP BY r.id_reservacion ORDER BY r.fecha_creacion DESC");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FilaVenta f = new FilaVenta();
                    f.numeroReservacion = rs.getString    ("numero_reservacion");
                    f.paquete           = rs.getString    ("paquete");
                    f.agente            = rs.getString    ("agente");
                    f.fechaViaje        = FechaUtil.toLocal(rs.getDate("fecha_viaje"));
                    f.cantidadPasajeros = rs.getInt       ("cantidad_pasajeros");
                    f.costoTotal        = rs.getBigDecimal("costo_total");
                    f.pasajeros         = rs.getString    ("pasajeros");
                    lista.add(f);
                }
            }
        }
        return lista;
    }
 
    // =================== REPORTE DE CANCELACIONES ===================
    public List<FilaCancelacion> reporteCancelaciones(LocalDate desde, LocalDate hasta) throws SQLException {
        List<FilaCancelacion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.numero_reservacion, p.nombre AS paquete, " +
                "ca.fecha_cancelacion, ca.monto_reembolsado, ca.porcentaje_reembolso, ca.perdida_agencia " +
                "FROM cancelacion ca " +
                "JOIN reservacion r ON ca.id_reservacion = r.id_reservacion " +
                "JOIN paquete p     ON r.id_paquete      = p.id_paquete " +
                "WHERE 1=1 ");
 
        if (desde != null) sql.append("AND ca.fecha_cancelacion >= ? ");
        if (hasta != null) sql.append("AND ca.fecha_cancelacion <= ? ");
        sql.append("ORDER BY ca.fecha_cancelacion DESC");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FilaCancelacion f = new FilaCancelacion();
                    f.numeroReservacion  = rs.getString    ("numero_reservacion");
                    f.paquete            = rs.getString    ("paquete");
                    f.fechaCancelacion   = FechaUtil.toLocal(rs.getDate("fecha_cancelacion"));
                    f.montoReembolsado   = rs.getBigDecimal("monto_reembolsado");
                    f.porcentajeReembolso= rs.getBigDecimal("porcentaje_reembolso");
                    f.perdidaAgencia     = rs.getBigDecimal("perdida_agencia");
                    lista.add(f);
                }
            }
        }
        return lista;
    }
 
    // =================== REPORTE DE GANANCIAS ===================
    public ResumenGanancias reporteGanancias(LocalDate desde, LocalDate hasta) throws SQLException {
        ResumenGanancias res = new ResumenGanancias();
 
        // Ganancia bruta: precio_venta - sum(costos proveedores) por reservacion confirmada
        StringBuilder sqlGanancia = new StringBuilder(
                "SELECT COALESCE(SUM(r.costo_total - (" +
                "  SELECT COALESCE(SUM(sp.costo_proveedor),0) FROM servicio_paquete sp WHERE sp.id_paquete = r.id_paquete" +
                ") * r.cantidad_pasajeros), 0) AS ganancia_bruta " +
                "FROM reservacion r WHERE r.id_estado = 2 ");
        if (desde != null) sqlGanancia.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sqlGanancia.append("AND r.fecha_creacion <= ? ");
 
        try (PreparedStatement ps = conn().prepareStatement(sqlGanancia.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) res.gananciaBruta = rs.getBigDecimal("ganancia_bruta");
            }
        }
 
        // Total reembolsos en el periodo
        StringBuilder sqlReembolso = new StringBuilder(
                "SELECT COALESCE(SUM(ca.monto_reembolsado), 0) AS total_reembolsos " +
                "FROM cancelacion ca WHERE 1=1 ");
        if (desde != null) sqlReembolso.append("AND ca.fecha_cancelacion >= ? ");
        if (hasta != null) sqlReembolso.append("AND ca.fecha_cancelacion <= ? ");
 
        try (PreparedStatement ps = conn().prepareStatement(sqlReembolso.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) res.totalReembolsos = rs.getBigDecimal("total_reembolsos");
            }
        }
 
        if (res.gananciaBruta != null && res.totalReembolsos != null) {
            res.gananciaNeta = res.gananciaBruta.subtract(res.totalReembolsos);
        }
 
        res.desde = desde;
        res.hasta  = hasta;
        return res;
    }
 
    // =================== AGENTE MÁS VENTAS ===================
    public AgenteReporte agenteMasVentas(LocalDate desde, LocalDate hasta) throws SQLException {
        AgenteReporte agente = new AgenteReporte();
        StringBuilder sql = new StringBuilder(
                "SELECT u.nombre AS agente, COUNT(r.id_reservacion) AS total_reservaciones, " +
                "SUM(r.costo_total) AS monto_total " +
                "FROM reservacion r JOIN usuario u ON r.id_usuario = u.id_usuario " +
                "WHERE r.id_estado = 2 ");
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
        sql.append("GROUP BY r.id_usuario ORDER BY total_reservaciones DESC LIMIT 1");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    agente.nombre             = rs.getString    ("agente");
                    agente.totalReservaciones = rs.getInt       ("total_reservaciones");
                    agente.montoTotal         = rs.getBigDecimal("monto_total");
                }
            }
        }
 
        // Detalle de reservaciones del agente top
        if (agente.nombre != null) {
            agente.reservaciones = reporteVentasPorAgente(agente.nombre, desde, hasta);
        }
        return agente;
    }
 
    // =================== AGENTE MÁS GANANCIAS ===================
    public AgenteReporte agenteMasGanancias(LocalDate desde, LocalDate hasta) throws SQLException {
        AgenteReporte agente = new AgenteReporte();
        StringBuilder sql = new StringBuilder(
                "SELECT u.nombre AS agente, " +
                "SUM(r.costo_total - (" +
                "  SELECT COALESCE(SUM(sp.costo_proveedor),0) FROM servicio_paquete sp WHERE sp.id_paquete = r.id_paquete" +
                ") * r.cantidad_pasajeros) AS ganancia_total " +
                "FROM reservacion r JOIN usuario u ON r.id_usuario = u.id_usuario " +
                "WHERE r.id_estado = 2 ");
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
        sql.append("GROUP BY r.id_usuario ORDER BY ganancia_total DESC LIMIT 1");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    agente.nombre      = rs.getString    ("agente");
                    agente.montoTotal  = rs.getBigDecimal("ganancia_total");
                }
            }
        }
        return agente;
    }
 
    // =================== PAQUETE MÁS VENDIDO ===================
    public PaqueteReporte paqueteMasVendido(LocalDate desde, LocalDate hasta) throws SQLException {
        return paqueteExtemo(desde, hasta, "DESC");
    }
 
    public PaqueteReporte paqueteMenosVendido(LocalDate desde, LocalDate hasta) throws SQLException {
        return paqueteExtemo(desde, hasta, "ASC");
    }
 
    private PaqueteReporte paqueteExtemo(LocalDate desde, LocalDate hasta, String orden) throws SQLException {
        PaqueteReporte pr = new PaqueteReporte();
        StringBuilder sql = new StringBuilder(
                "SELECT p.nombre AS paquete, COUNT(r.id_reservacion) AS total_reservaciones " +
                "FROM reservacion r JOIN paquete p ON r.id_paquete = p.id_paquete " +
                "WHERE r.id_estado = 2 ");
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
        sql.append("GROUP BY r.id_paquete ORDER BY total_reservaciones ").append(orden).append(" LIMIT 1");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pr.nombre             = rs.getString("paquete");
                    pr.totalReservaciones = rs.getInt   ("total_reservaciones");
                }
            }
        }
        return pr;
    }
 
    // =================== OCUPACIÓN POR DESTINO ===================
    public List<OcupacionDestino> reporteOcupacionDestino(LocalDate desde, LocalDate hasta) throws SQLException {
        List<OcupacionDestino> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT d.nombre AS destino, COUNT(r.id_reservacion) AS total_reservaciones, " +
                "SUM(r.cantidad_pasajeros) AS total_pasajeros " +
                "FROM reservacion r " +
                "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                "JOIN destino d ON p.id_destino = d.id_destino " +
                "WHERE r.id_estado IN (2, 4) "); // Confirmada y Completada
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
        sql.append("GROUP BY d.id_destino ORDER BY total_reservaciones DESC");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OcupacionDestino od = new OcupacionDestino();
                    od.destino             = rs.getString("destino");
                    od.totalReservaciones  = rs.getInt   ("total_reservaciones");
                    od.totalPasajeros      = rs.getInt   ("total_pasajeros");
                    lista.add(od);
                }
            }
        }
        return lista;
    }
 
    // =================== AUXILIAR: reservaciones de un agente ===================
    private List<FilaVenta> reporteVentasPorAgente(String nombreAgente, LocalDate desde, LocalDate hasta)
            throws SQLException {
        List<FilaVenta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.numero_reservacion, p.nombre AS paquete, u.nombre AS agente, " +
                "r.fecha_viaje, r.cantidad_pasajeros, r.costo_total, '' AS pasajeros " +
                "FROM reservacion r " +
                "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                "WHERE r.id_estado = 2 AND u.nombre = ? ");
        if (desde != null) sql.append("AND r.fecha_creacion >= ? ");
        if (hasta != null) sql.append("AND r.fecha_creacion <= ? ");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, nombreAgente);
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FilaVenta f = new FilaVenta();
                    f.numeroReservacion = rs.getString    ("numero_reservacion");
                    f.paquete           = rs.getString    ("paquete");
                    f.agente            = rs.getString    ("agente");
                    f.fechaViaje        = FechaUtil.toLocal(rs.getDate("fecha_viaje"));
                    f.cantidadPasajeros = rs.getInt       ("cantidad_pasajeros");
                    f.costoTotal        = rs.getBigDecimal("costo_total");
                    lista.add(f);
                }
            }
        }
        return lista;
    }
}
