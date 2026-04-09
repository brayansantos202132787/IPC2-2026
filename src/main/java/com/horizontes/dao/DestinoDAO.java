/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Destino;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class DestinoDAO {
     private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    public Destino buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM destino WHERE id_destino = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public Destino buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM destino WHERE nombre = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Destino> listarTodos() throws SQLException {
        List<Destino> lista = new ArrayList<>();
        String sql = "SELECT * FROM destino ORDER BY nombre";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    public int insertar(Destino d) throws SQLException {
        String sql = "INSERT INTO destino (nombre, pais, descripcion, clima, mejor_epoca, imagen_url) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getPais());
            ps.setString(3, d.getDescripcion());
            ps.setString(4, d.getClima());
            ps.setString(5, d.getMejorEpoca());
            ps.setString(6, d.getImagenUrl());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    public boolean actualizar(Destino d) throws SQLException {
        String sql = "UPDATE destino SET nombre=?, pais=?, descripcion=?, clima=?, mejor_epoca=?, imagen_url=? " +
                     "WHERE id_destino=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getPais());
            ps.setString(3, d.getDescripcion());
            ps.setString(4, d.getClima());
            ps.setString(5, d.getMejorEpoca());
            ps.setString(6, d.getImagenUrl());
            ps.setInt   (7, d.getIdDestino());
            return ps.executeUpdate() > 0;
        }
    }
 
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM destino WHERE id_destino = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
 
    private Destino mapear(ResultSet rs) throws SQLException {
        Destino d = new Destino();
        d.setIdDestino  (rs.getInt   ("id_destino"));
        d.setNombre     (rs.getString("nombre"));
        d.setPais       (rs.getString("pais"));
        d.setDescripcion(rs.getString("descripcion"));
        d.setClima      (rs.getString("clima"));
        d.setMejorEpoca (rs.getString("mejor_epoca"));
        d.setImagenUrl  (rs.getString("imagen_url"));
        return d;
    }
}
