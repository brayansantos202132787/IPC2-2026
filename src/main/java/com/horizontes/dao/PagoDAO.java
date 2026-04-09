/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Pago;
import com.horizontes.utils.FechaUtil;
 
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class PagoDAO {
     private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT pg.id_pago, pg.id_reservacion, r.numero_reservacion, " +
            "pg.monto, pg.id_metodo, mp.nombre AS nombre_metodo, pg.fecha_pago " +
            "FROM pago pg " +
            "JOIN reservacion r  ON pg.id_reservacion = r.id_reservacion " +
            "JOIN metodo_pago mp ON pg.id_metodo      = mp.id_metodo ";
 
    public Pago buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(SELECT_BASE + "WHERE pg.id_pago = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Pago> listarPorReservacion(int idReservacion) throws SQLException {
        List<Pago> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
                SELECT_BASE + "WHERE pg.id_reservacion = ? ORDER BY pg.fecha_pago")) {
            ps.setInt(1, idReservacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    /** Suma el total pagado para una reservación. */
    public BigDecimal totalPagado(int idReservacion) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) AS total FROM pago WHERE id_reservacion = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, idReservacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }
 
    public int insertar(Pago pago) throws SQLException {
        String sql = "INSERT INTO pago (id_reservacion, monto, id_metodo, fecha_pago) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, pago.getIdReservacion());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setInt       (3, pago.getIdMetodo());
            ps.setDate      (4, FechaUtil.toSql(pago.getFechaPago()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    private Pago mapear(ResultSet rs) throws SQLException {
        Pago p = new Pago();
        p.setIdPago           (rs.getInt       ("id_pago"));
        p.setIdReservacion    (rs.getInt       ("id_reservacion"));
        p.setNumeroReservacion(rs.getString    ("numero_reservacion"));
        p.setMonto            (rs.getBigDecimal("monto"));
        p.setIdMetodo         (rs.getInt       ("id_metodo"));
        p.setNombreMetodo     (rs.getString    ("nombre_metodo"));
        p.setFechaPago        (FechaUtil.toLocal(rs.getDate("fecha_pago")));
        return p;
    }
}
