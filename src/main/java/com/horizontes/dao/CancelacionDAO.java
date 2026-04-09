/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.dao;

import com.horizontes.config.DBConnection;
import com.horizontes.models.Cancelacion;
import com.horizontes.utils.FechaUtil;
 
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class CancelacionDAO {
    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }
 
    private static final String SELECT_BASE =
            "SELECT c.id_cancelacion, c.id_reservacion, r.numero_reservacion, " +
            "c.fecha_cancelacion, c.monto_reembolsado, c.porcentaje_reembolso, c.perdida_agencia " +
            "FROM cancelacion c JOIN reservacion r ON c.id_reservacion = r.id_reservacion ";
 
    public Cancelacion buscarPorReservacion(int idReservacion) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                SELECT_BASE + "WHERE c.id_reservacion = ?")) {
            ps.setInt(1, idReservacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }
 
    public List<Cancelacion> listarEnPeriodo(LocalDate desde, LocalDate hasta) throws SQLException {
        List<Cancelacion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE + "WHERE 1=1 ");
        if (desde != null) sql.append("AND c.fecha_cancelacion >= ? ");
        if (hasta != null) sql.append("AND c.fecha_cancelacion <= ? ");
        sql.append("ORDER BY c.fecha_cancelacion DESC");
 
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            int idx = 1;
            if (desde != null) ps.setDate(idx++, FechaUtil.toSql(desde));
            if (hasta != null) ps.setDate(idx,   FechaUtil.toSql(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }
 
    public int insertar(Cancelacion c) throws SQLException {
        String sql = "INSERT INTO cancelacion " +
                     "(id_reservacion, fecha_cancelacion, monto_reembolsado, porcentaje_reembolso, perdida_agencia) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, c.getIdReservacion());
            ps.setDate      (2, FechaUtil.toSql(c.getFechaCancelacion()));
            ps.setBigDecimal(3, c.getMontoReembolsado());
            ps.setBigDecimal(4, c.getPorcentajeReembolso());
            ps.setBigDecimal(5, c.getPerdidaAgencia());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
 
    private Cancelacion mapear(ResultSet rs) throws SQLException {
        Cancelacion c = new Cancelacion();
        c.setIdCancelacion      (rs.getInt       ("id_cancelacion"));
        c.setIdReservacion      (rs.getInt       ("id_reservacion"));
        c.setNumeroReservacion  (rs.getString    ("numero_reservacion"));
        c.setFechaCancelacion   (FechaUtil.toLocal(rs.getDate("fecha_cancelacion")));
        c.setMontoReembolsado   (rs.getBigDecimal("monto_reembolsado"));
        c.setPorcentajeReembolso(rs.getBigDecimal("porcentaje_reembolso"));
        c.setPerdidaAgencia     (rs.getBigDecimal("perdida_agencia"));
        return c;
    }
}
