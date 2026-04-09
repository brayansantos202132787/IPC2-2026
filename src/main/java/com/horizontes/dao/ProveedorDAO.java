/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Proveedor;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ProveedorDAO {
    
      private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT p.id_proveedor, p.nombre, p.id_tipo, tp.nombre AS nombre_tipo, p.pais, p.contacto " +
            "FROM proveedor p JOIN tipo_proveedor tp ON p.id_tipo = tp.id_tipo ";
 
    public Proveedor buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE p.id_proveedor = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public Proveedor buscarPorNombre(String nombre) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE p.nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Proveedor> listarTodos() throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(SELECT_BASE + "ORDER BY p.nombre")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    public List<Proveedor> listarPorTipo(int idTipo) throws SQLException {
        List<Proveedor> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE p.id_tipo = ? ORDER BY p.nombre")) {
            ps.setInt(1, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    public int insertar(Proveedor p) throws SQLException {
        String sql = "INSERT INTO proveedor (nombre, id_tipo, pais, contacto) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setInt   (2, p.getIdTipo());
            ps.setString(3, p.getPais());
            ps.setString(4, p.getContacto());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    public boolean actualizar(Proveedor p) throws SQLException {
        String sql = "UPDATE proveedor SET nombre=?, id_tipo=?, pais=?, contacto=? WHERE id_proveedor=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setInt   (2, p.getIdTipo());
            ps.setString(3, p.getPais());
            ps.setString(4, p.getContacto());
            ps.setInt   (5, p.getIdProveedor());
            return ps.executeUpdate() > 0;
        }
    }
 
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
 
    private Proveedor mapear(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt   ("id_proveedor"));
        p.setNombre     (rs.getString("nombre"));
        p.setIdTipo     (rs.getInt   ("id_tipo"));
        p.setNombreTipo (rs.getString("nombre_tipo"));
        p.setPais       (rs.getString("pais"));
        p.setContacto   (rs.getString("contacto"));
        return p;
    }
}
