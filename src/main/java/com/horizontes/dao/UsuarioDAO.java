/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Usuario;
import com.horizontes.utils.FechaUtil;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class UsuarioDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    
    public Usuario buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.password, u.id_rol, r.nombre AS nombre_rol, u.activo " +
                     "FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE u.nombre = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    // =================== BUSCAR POR ID ===================
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nombre, u.password, u.id_rol, r.nombre AS nombre_rol, u.activo " +
                     "FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE u.id_usuario = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.id_usuario, u.nombre, u.password, u.id_rol, r.nombre AS nombre_rol, u.activo " +
                     "FROM usuario u JOIN rol r ON u.id_rol = r.id_rol ORDER BY u.nombre";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    
    public int insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, password, id_rol, activo) VALUES (?,?,?,1)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            ps.setInt   (3, u.getIdRol());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    
    public boolean actualizarRol(int idUsuario, int idRol) throws SQLException {
        String sql = "UPDATE usuario SET id_rol = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
 
    
    public boolean cambiarPassword(int idUsuario, String hashNuevo) throws SQLException {
        String sql = "UPDATE usuario SET password = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, hashNuevo);
            ps.setInt   (2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
 
  
    public boolean desactivar(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET activo = 0 WHERE id_usuario = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
 
    
    public boolean activar(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET activo = 1 WHERE id_usuario = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
 
    
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE nombre = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
 
    
    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt   ("id_usuario"));
        u.setNombre   (rs.getString("nombre"));
        u.setPassword (rs.getString("password"));
        u.setIdRol    (rs.getInt   ("id_rol"));
        u.setNombreRol(rs.getString("nombre_rol"));
        u.setActivo   (rs.getInt   ("activo") == 1);
        return u;
    }
}
