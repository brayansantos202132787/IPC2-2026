/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.config.AppConfig;
import com.horizontes.dao.CancelacionDAO;
import com.horizontes.dao.PagoDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Cancelacion;
import com.horizontes.models.Reservacion;
import com.horizontes.utils.FechaUtil;
 
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author braya
 */
public class CancelacionService {
    
        private static final int ESTADO_PENDIENTE  = 1;
    private static final int ESTADO_CONFIRMADA = 2;
    private static final int ESTADO_CANCELADA  = 3;
 
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final PagoDAO        pagoDAO        = new PagoDAO();
    private final CancelacionDAO cancelacionDAO = new CancelacionDAO();
 
    // =================== PROCESAR CANCELACIÓN ===================
    /**
     * Cancela una reservación, calcula el reembolso y registra la cancelación.
     * @param idReservacion ID de la reservación a cancelar.
     * @return La entidad Cancelacion creada.
     */
    public Cancelacion procesar(int idReservacion) {
        try {
            Reservacion r = reservacionDAO.buscarPorId(idReservacion);
            if (r == null) throw new ApiException(404, "Reservación no encontrada.");
 
            // Validar estado
            if (r.getIdEstado() != ESTADO_PENDIENTE && r.getIdEstado() != ESTADO_CONFIRMADA) {
                throw new ValidationException(
                    "Solo se pueden cancelar reservaciones en estado Pendiente o Confirmada.");
            }
 
            // Validar días de anticipación
            LocalDate hoy       = FechaUtil.hoy();
            long      diasAntes = FechaUtil.diasEntre(hoy, r.getFechaViaje());
 
            if (diasAntes < AppConfig.CANCEL_DIAS_MIN) {
                throw new ValidationException(
                    "No se puede cancelar. El viaje es en menos de " +
                    AppConfig.CANCEL_DIAS_MIN + " días.");
            }
 
            // Calcular porcentaje de reembolso
            double porcentaje = calcularPorcentaje(diasAntes);
 
            // Total pagado hasta el momento
            BigDecimal totalPagado = pagoDAO.totalPagado(idReservacion);
 
            BigDecimal porcDecimal       = BigDecimal.valueOf(porcentaje / 100.0);
            BigDecimal montoReembolsado  = totalPagado.multiply(porcDecimal)
                                                      .setScale(2, RoundingMode.HALF_UP);
            BigDecimal perdidaAgencia    = totalPagado.subtract(montoReembolsado);
 
            // Registrar cancelación
            Cancelacion cancelacion = new Cancelacion();
            cancelacion.setIdReservacion      (idReservacion);
            cancelacion.setFechaCancelacion   (hoy);
            cancelacion.setMontoReembolsado   (montoReembolsado);
            cancelacion.setPorcentajeReembolso(BigDecimal.valueOf(porcentaje));
            cancelacion.setPerdidaAgencia     (perdidaAgencia);
 
            int idCancelacion = cancelacionDAO.insertar(cancelacion);
            cancelacion.setIdCancelacion(idCancelacion);
            cancelacion.setNumeroReservacion(r.getNumeroReservacion());
 
            // Cambiar estado de la reservación a Cancelada
            reservacionDAO.cambiarEstado(idReservacion, ESTADO_CANCELADA);
 
            return cancelacion;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al procesar cancelación: " + e.getMessage(), e);
        }
    }
 
    // =================== CALCULAR PREVIAMENTE ===================
    /**
     * Devuelve una cancelación simulada (sin persistir) para mostrar
     * al agente antes de confirmar.
     */
    public Cancelacion simular(int idReservacion) {
        try {
            Reservacion r = reservacionDAO.buscarPorId(idReservacion);
            if (r == null) throw new ApiException(404, "Reservación no encontrada.");
 
            if (r.getIdEstado() != ESTADO_PENDIENTE && r.getIdEstado() != ESTADO_CONFIRMADA) {
                throw new ValidationException(
                    "Solo se pueden cancelar reservaciones en estado Pendiente o Confirmada.");
            }
 
            LocalDate hoy       = FechaUtil.hoy();
            long      diasAntes = FechaUtil.diasEntre(hoy, r.getFechaViaje());
 
            if (diasAntes < AppConfig.CANCEL_DIAS_MIN) {
                throw new ValidationException(
                    "No se puede cancelar. El viaje es en menos de " +
                    AppConfig.CANCEL_DIAS_MIN + " días.");
            }
 
            double porcentaje    = calcularPorcentaje(diasAntes);
            BigDecimal totalPagado = pagoDAO.totalPagado(idReservacion);
            BigDecimal porcDecimal = BigDecimal.valueOf(porcentaje / 100.0);
            BigDecimal monto       = totalPagado.multiply(porcDecimal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal perdida     = totalPagado.subtract(monto);
 
            Cancelacion sim = new Cancelacion();
            sim.setIdReservacion      (idReservacion);
            sim.setNumeroReservacion  (r.getNumeroReservacion());
            sim.setFechaCancelacion   (hoy);
            sim.setMontoReembolsado   (monto);
            sim.setPorcentajeReembolso(BigDecimal.valueOf(porcentaje));
            sim.setPerdidaAgencia     (perdida);
            return sim;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al simular cancelación: " + e.getMessage(), e);
        }
    }
 
    // =================== LISTAR ===================
    public List<Cancelacion> listarEnPeriodo(String desdeStr, String hastaStr) {
        try {
            LocalDate desde = (desdeStr != null && !desdeStr.isBlank()) ? FechaUtil.parsearIso(desdeStr) : null;
            LocalDate hasta = (hastaStr != null && !hastaStr.isBlank()) ? FechaUtil.parsearIso(hastaStr) : null;
            return cancelacionDAO.listarEnPeriodo(desde, hasta);
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar cancelaciones: " + e.getMessage(), e);
        }
    }
 
    
    private double calcularPorcentaje(long diasAntes) {
        if (diasAntes > AppConfig.CANCEL_DIAS_100)    return AppConfig.CANCEL_PORCENTAJE_100;
        if (diasAntes >= AppConfig.CANCEL_DIAS_70_MIN) return AppConfig.CANCEL_PORCENTAJE_70;
        if (diasAntes >= AppConfig.CANCEL_DIAS_40_MIN) return AppConfig.CANCEL_PORCENTAJE_40;
        throw new ValidationException(
            "No se puede cancelar. El viaje es en menos de " + AppConfig.CANCEL_DIAS_MIN + " días.");
    }
}
