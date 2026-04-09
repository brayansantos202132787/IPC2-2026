/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Cliente;
import com.horizontes.models.Reservacion;
import com.horizontes.utils.FechaUtil;
 
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ReservacionDAO {
    
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
            "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
            "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
            "FROM reservacion r " +
            "JOIN paquete p ON r.id_paquete = r.id_paquete " +
            "JOIN usuario u ON r.id_usuario = u.id_usuario " +
            "JOIN estado_reservacion e ON r.id_estado = e.id_estado ";
 
    // =================== BUSCAR ===================
    public Reservacion buscarPorId(int id) throws SQLException {
        String sql = "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
                     "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
                     "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                     "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                     "JOIN estado_reservacion e ON r.id_estado = e.id_estado " +
                     "WHERE r.id_reservacion = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public Reservacion buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
                     "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
                     "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                     "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                     "JOIN estado_reservacion e ON r.id_estado = e.id_estado " +
                     "WHERE r.numero_reservacion = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Reservacion> listarTodas() throws SQLException {
        return listarConFiltros(null, null, null, -1, -1);
    }
 
    public List<Reservacion> listarDelDia() throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
                     "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
                     "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                     "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                     "JOIN estado_reservacion e ON r.id_estado = e.id_estado " +
                     "WHERE r.fecha_viaje = CURDATE() ORDER BY r.numero_reservacion";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }
 
    public List<Reservacion> listarPorCliente(int idCliente) throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        String sql = "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
                     "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
                     "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
                     "FROM reservacion r " +
                     "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                     "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                     "JOIN estado_reservacion e ON r.id_estado = e.id_estado " +
                     "JOIN reservacion_pasajero rp ON r.id_reservacion = rp.id_reservacion " +
                     "WHERE rp.id_cliente = ? ORDER BY r.fecha_creacion DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    public List<Reservacion> listarConFiltros(LocalDate desde, LocalDate hasta,
                                               Integer idEstado, int idDestino, int idPaquete)
            throws SQLException {
        List<Reservacion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.id_reservacion, r.numero_reservacion, r.id_paquete, p.nombre AS nombre_paquete, " +
                "r.id_usuario, u.nombre AS nombre_usuario, r.fecha_creacion, r.fecha_viaje, " +
                "r.cantidad_pasajeros, r.costo_total, r.id_estado, e.nombre AS nombre_estado " +
                "FROM reservacion r " +
                "JOIN paquete p ON r.id_paquete = p.id_paquete " +
                "JOIN usuario u ON r.id_usuario = u.id_usuario " +
                "JOIN estado_reservacion e ON r.id_estado = e.id_estado WHERE 1=1 ");
 
        if (desde    != null)  sql.append("AND r.fecha_creacion >= ? ");
        if (hasta    != null)  sql.append("AND r.fecha_creacion <= ? ");
        if (idEstado != null)  sql.append("AND r.id_estado = ? ");
        if (idDestino > 0)     sql.append("AND p.id_destino = ? ");
        if (idPaquete > 0)     sql.append("AND r.id_paquete = ? ");
        sql.append("ORDER BY r.fecha_creacion DESC");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde    != null)  ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta    != null)  ps.setDate(idx++, FechaUtil.toSql(hasta));
            if (idEstado != null)  ps.setInt (idx++, idEstado);
            if (idDestino > 0)     ps.setInt (idx++, idDestino);
            if (idPaquete > 0)     ps.setInt (idx,   idPaquete);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    // =================== INSERTAR ===================
    public int insertar(Reservacion r) throws SQLException {
        String sql = "INSERT INTO reservacion " +
                     "(numero_reservacion, id_paquete, id_usuario, fecha_creacion, fecha_viaje, " +
                     "cantidad_pasajeros, costo_total, id_estado) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString    (1, r.getNumeroReservacion());
            ps.setInt       (2, r.getIdPaquete());
            ps.setInt       (3, r.getIdUsuario());
            ps.setDate      (4, FechaUtil.toSql(r.getFechaCreacion()));
            ps.setDate      (5, FechaUtil.toSql(r.getFechaViaje()));
            ps.setInt       (6, r.getCantidadPasajeros());
            ps.setBigDecimal(7, r.getCostoTotal());
            ps.setInt       (8, r.getIdEstado());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    // =================== INSERTAR PASAJERO ===================
    public void insertarPasajero(int idReservacion, int idCliente) throws SQLException {
        String sql = "INSERT IGNORE INTO reservacion_pasajero (id_reservacion, id_cliente) VALUES (?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idReservacion);
            ps.setInt(2, idCliente);
            ps.executeUpdate();
        }
    }
 
    // =================== CAMBIAR ESTADO ===================
    public boolean cambiarEstado(int idReservacion, int idEstado) throws SQLException {
        String sql = "UPDATE reservacion SET id_estado = ? WHERE id_reservacion = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setInt(2, idReservacion);
            return ps.executeUpdate() > 0;
        }
    }
 
    // =================== OBTENER PASAJEROS ===================
    public List<Cliente> obtenerPasajeros(int idReservacion) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT c.* FROM cliente c " +
                     "JOIN reservacion_pasajero rp ON c.id_cliente = rp.id_cliente " +
                     "WHERE rp.id_reservacion = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idReservacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente   (rs.getInt   ("id_cliente"));
                    c.setDpi         (rs.getString("dpi"));
                    c.setNombre      (rs.getString("nombre"));
                    c.setFechaNac    (FechaUtil.toLocal(rs.getDate("fecha_nac")));
                    c.setTelefono    (rs.getString("telefono"));
                    c.setEmail       (rs.getString("email"));
                    c.setNacionalidad(rs.getString("nacionalidad"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }
 
    // =================== GENERAR NÚMERO ÚNICO ===================
    public String generarNumeroReservacion() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM reservacion";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int total = rs.getInt("total") + 1;
                return String.format("RES-%05d", total);
            }
        }
        return "RES-00001";
    }
 
    private Reservacion mapear(ResultSet rs) throws SQLException {
        Reservacion r = new Reservacion();
        r.setIdReservacion    (rs.getInt       ("id_reservacion"));
        r.setNumeroReservacion(rs.getString    ("numero_reservacion"));
        r.setIdPaquete        (rs.getInt       ("id_paquete"));
        r.setNombrePaquete    (rs.getString    ("nombre_paquete"));
        r.setIdUsuario        (rs.getInt       ("id_usuario"));
        r.setNombreUsuario    (rs.getString    ("nombre_usuario"));
        r.setFechaCreacion    (FechaUtil.toLocal(rs.getDate("fecha_creacion")));
        r.setFechaViaje       (FechaUtil.toLocal(rs.getDate("fecha_viaje")));
        r.setCantidadPasajeros(rs.getInt       ("cantidad_pasajeros"));
        r.setCostoTotal       (rs.getBigDecimal("costo_total"));
        r.setIdEstado         (rs.getInt       ("id_estado"));
        r.setNombreEstado     (rs.getString    ("nombre_estado"));
        return r;
    }
}
