/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Paquete;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class PaqueteDAO {
    
     private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT p.id_paquete, p.nombre, p.id_destino, d.nombre AS nombre_destino, " +
            "p.duracion_dias, p.descripcion, p.precio_venta, p.capacidad, p.activo " +
            "FROM paquete p JOIN destino d ON p.id_destino = d.id_destino ";
 
    public Paquete buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE p.id_paquete = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public Paquete buscarPorNombre(String nombre) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE p.nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Paquete> listarTodos() throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY p.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    public List<Paquete> listarActivos() throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "WHERE p.activo = 1 ORDER BY p.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    public List<Paquete> listarPorDestino(int idDestino) throws SQLException {
        List<Paquete> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                SELECT_BASE + "WHERE p.id_destino = ? AND p.activo = 1 ORDER BY p.nombre")) {
            ps.setInt(1, idDestino);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    public int insertar(Paquete p) throws SQLException {
        String sql = "INSERT INTO paquete (nombre, id_destino, duracion_dias, descripcion, precio_venta, capacidad, activo) " +
                     "VALUES (?,?,?,?,?,?,1)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString    (1, p.getNombre());
            ps.setInt       (2, p.getIdDestino());
            ps.setInt       (3, p.getDuracionDias());
            ps.setString    (4, p.getDescripcion());
            ps.setBigDecimal(5, p.getPrecioVenta());
            ps.setInt       (6, p.getCapacidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    public boolean actualizar(Paquete p) throws SQLException {
        String sql = "UPDATE paquete SET nombre=?, id_destino=?, duracion_dias=?, descripcion=?, " +
                     "precio_venta=?, capacidad=? WHERE id_paquete=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString    (1, p.getNombre());
            ps.setInt       (2, p.getIdDestino());
            ps.setInt       (3, p.getDuracionDias());
            ps.setString    (4, p.getDescripcion());
            ps.setBigDecimal(5, p.getPrecioVenta());
            ps.setInt       (6, p.getCapacidad());
            ps.setInt       (7, p.getIdPaquete());
            return ps.executeUpdate() > 0;
        }
    }
 
    public boolean cambiarEstado(int idPaquete, boolean activo) throws SQLException {
        String sql = "UPDATE paquete SET activo = ? WHERE id_paquete = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setInt(2, idPaquete);
            return ps.executeUpdate() > 0;
        }
    }
 
    /**
     * Cuenta los pasajeros con reservaciones Pendiente o Confirmada para
     * un paquete con fecha de viaje futura (para alerta de alta demanda).
     */
    public int contarPasajerosFuturos(int idPaquete) throws SQLException {
        String sql = "SELECT COALESCE(SUM(r.cantidad_pasajeros), 0) AS total " +
                     "FROM reservacion r " +
                     "WHERE r.id_paquete = ? " +
                     "  AND r.id_estado IN (1, 2) " +   // Pendiente=1, Confirmada=2
                     "  AND r.fecha_viaje >= CURDATE()";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idPaquete);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }
 
    private Paquete mapear(ResultSet rs) throws SQLException {
        Paquete p = new Paquete();
        p.setIdPaquete    (rs.getInt       ("id_paquete"));
        p.setNombre       (rs.getString    ("nombre"));
        p.setIdDestino    (rs.getInt       ("id_destino"));
        p.setNombreDestino(rs.getString    ("nombre_destino"));
        p.setDuracionDias (rs.getInt       ("duracion_dias"));
        p.setDescripcion  (rs.getString    ("descripcion"));
        p.setPrecioVenta  (rs.getBigDecimal("precio_venta"));
        p.setCapacidad    (rs.getInt       ("capacidad"));
        p.setActivo       (rs.getInt       ("activo") == 1);
        return p;
    }
}
