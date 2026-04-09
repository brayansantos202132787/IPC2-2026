/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.dao.PagoDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Pago;
import com.horizontes.models.Reservacion;
import com.horizontes.utils.FechaUtil;
import com.horizontes.utils.Validaciones;
 
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author braya
 */
public class PagoService {
    
    private static final int ESTADO_CONFIRMADA = 2;
 
    private final PagoDAO        pagoDAO        = new PagoDAO();
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
 
    // =================== REGISTRAR PAGO ===================
    /**
     * Registra un pago parcial o total.
     * Si el total pagado cubre el costo, la reservación pasa a Confirmada.
     */
    public Pago registrar(int idReservacion, BigDecimal monto, int idMetodo, String fechaStr) {
        // Validaciones
        Validaciones.positivo(monto, "monto");
        if (idMetodo < 1 || idMetodo > 3) throw new ValidationException("Método de pago inválido (1-3).");
 
        LocalDate fechaPago = (fechaStr != null && !fechaStr.isBlank())
                ? FechaUtil.parsearIso(fechaStr)
                : FechaUtil.hoy();
 
        try {
            Reservacion reservacion = reservacionDAO.buscarPorId(idReservacion);
            if (reservacion == null) throw new ApiException(404, "Reservación no encontrada.");
 
          
            if (reservacion.getIdEstado() == 3) throw new ValidationException("No se puede pagar una reservación cancelada.");
            if (reservacion.getIdEstado() == 4) throw new ValidationException("La reservación ya está completada.");
 
            // Verificar que el monto no exceda el saldo pendiente
            BigDecimal totalPagado  = pagoDAO.totalPagado(idReservacion);
            BigDecimal saldoPendiente = reservacion.getCostoTotal().subtract(totalPagado);
 
            if (monto.compareTo(saldoPendiente) > 0) {
                throw new ValidationException(
                    "El monto excede el saldo pendiente. Saldo pendiente: Q. " + saldoPendiente);
            }
 
            // Insertar pago
            Pago pago = new Pago();
            pago.setIdReservacion(idReservacion);
            pago.setMonto        (monto);
            pago.setIdMetodo     (idMetodo);
            pago.setFechaPago    (fechaPago);
 
            int idPago = pagoDAO.insertar(pago);
            pago.setIdPago(idPago);
 
            // Verificar si el pago completa la reservación
            BigDecimal nuevoTotal = totalPagado.add(monto);
            if (nuevoTotal.compareTo(reservacion.getCostoTotal()) >= 0) {
                reservacionDAO.cambiarEstado(idReservacion, ESTADO_CONFIRMADA);
            }
 
            return pago;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al registrar pago: " + e.getMessage(), e);
        }
    }
 
    // =================== LISTAR POR RESERVACIÓN ===================
    public List<Pago> listarPorReservacion(int idReservacion) {
        try {
            return pagoDAO.listarPorReservacion(idReservacion);
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar pagos: " + e.getMessage(), e);
        }
    }
 
    
    public BigDecimal totalPagado(int idReservacion) {
        try {
            return pagoDAO.totalPagado(idReservacion);
        } catch (SQLException e) {
            throw new ApiException(500, "Error al calcular total pagado: " + e.getMessage(), e);
        }
    }
 
    // =================== SALDO PENDIENTE ===================
    public BigDecimal saldoPendiente(int idReservacion) {
        try {
            Reservacion r = reservacionDAO.buscarPorId(idReservacion);
            if (r == null) throw new ApiException(404, "Reservación no encontrada.");
            BigDecimal pagado = pagoDAO.totalPagado(idReservacion);
            BigDecimal saldo  = r.getCostoTotal().subtract(pagado);
            return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al calcular saldo: " + e.getMessage(), e);
        }
    }
 
    
    public List<Pago> obtenerPagosConTotal(int idReservacion) {
        try {
            if (reservacionDAO.buscarPorId(idReservacion) == null) {
                throw new ApiException(404, "Reservación no encontrada.");
            }
            return pagoDAO.listarPorReservacion(idReservacion);
        } catch (SQLException e) {
            throw new ApiException(500, "Error al obtener pagos: " + e.getMessage(), e);
        }
    }
}
