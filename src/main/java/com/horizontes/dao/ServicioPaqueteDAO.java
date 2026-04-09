/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.ServicioPaquete;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ServicioPaqueteDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT sp.id_servicio, sp.id_paquete, sp.id_proveedor, " +
            "pr.nombre AS nombre_proveedor, sp.descripcion, sp.costo_proveedor " +
            "FROM servicio_paquete sp JOIN proveedor pr ON sp.id_proveedor = pr.id_proveedor ";
 
    public List<ServicioPaquete> listarPorPaquete(int idPaquete) throws SQLException {
        List<ServicioPaquete> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE sp.id_paquete = ?")) {
            ps.setInt(1, idPaquete);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    public ServicioPaquete buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE sp.id_servicio = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public int insertar(ServicioPaquete sp) throws SQLException {
        String sql = "INSERT INTO servicio_paquete (id_paquete, id_proveedor, descripcion, costo_proveedor) " +
                     "VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, sp.getIdPaquete());
            ps.setInt       (2, sp.getIdProveedor());
            ps.setString    (3, sp.getDescripcion());
            ps.setBigDecimal(4, sp.getCostoProveedor());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    public boolean actualizar(ServicioPaquete sp) throws SQLException {
        String sql = "UPDATE servicio_paquete SET id_proveedor=?, descripcion=?, costo_proveedor=? " +
                     "WHERE id_servicio=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt       (1, sp.getIdProveedor());
            ps.setString    (2, sp.getDescripcion());
            ps.setBigDecimal(3, sp.getCostoProveedor());
            ps.setInt       (4, sp.getIdServicio());
            return ps.executeUpdate() > 0;
        }
    }
 
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM servicio_paquete WHERE id_servicio = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
 
    public boolean eliminarPorPaquete(int idPaquete) throws SQLException {
        String sql = "DELETE FROM servicio_paquete WHERE id_paquete = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idPaquete);
            return ps.executeUpdate() >= 0;
        }
    }
 
    private ServicioPaquete mapear(ResultSet rs) throws SQLException {
        ServicioPaquete sp = new ServicioPaquete();
        sp.setIdServicio     (rs.getInt       ("id_servicio"));
        sp.setIdPaquete      (rs.getInt       ("id_paquete"));
        sp.setIdProveedor    (rs.getInt       ("id_proveedor"));
        sp.setNombreProveedor(rs.getString    ("nombre_proveedor"));
        sp.setDescripcion    (rs.getString    ("descripcion"));
        sp.setCostoProveedor (rs.getBigDecimal("costo_proveedor"));
        return sp;
    }
}
