/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Cliente;
import com.horizontes.utils.FechaUtil;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author braya
 */
public class ClienteDAO {
     private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    
    public Cliente buscarPorDpi(String dpi) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE dpi = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, dpi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    // =================== BUSCAR POR ID ===================
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    // =================== LISTAR TODOS ===================
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nombre";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    // =================== BUSCAR POR NOMBRE (búsqueda parcial) ===================
    public List<Cliente> buscarPorNombre(String fragmento) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nombre LIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + fragmento + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    // =================== INSERTAR ===================
    public int insertar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (dpi, nombre, fecha_nac, telefono, email, nacionalidad) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getDpi());
            ps.setString(2, c.getNombre());
            ps.setDate  (3, FechaUtil.toSql(c.getFechaNac()));
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getNacionalidad());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    // =================== ACTUALIZAR ===================
    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, fecha_nac=?, telefono=?, email=?, nacionalidad=? " +
                     "WHERE id_cliente=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setDate  (2, FechaUtil.toSql(c.getFechaNac()));
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getNacionalidad());
            ps.setInt   (6, c.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }
 
    // =================== HISTORIAL DE RESERVACIONES (IDs) ===================
    public List<Integer> obtenerIdsReservaciones(int idCliente) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_reservacion FROM reservacion_pasajero WHERE id_cliente = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("id_reservacion"));
            }
        }
        return ids;
    }
 
    // =================== MAPEO ===================
    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente   (rs.getInt   ("id_cliente"));
        c.setDpi         (rs.getString("dpi"));
        c.setNombre      (rs.getString("nombre"));
        c.setFechaNac    (FechaUtil.toLocal(rs.getDate("fecha_nac")));
        c.setTelefono    (rs.getString("telefono"));
        c.setEmail       (rs.getString("email"));
        c.setNacionalidad(rs.getString("nacionalidad"));
        return c;
    }
}
