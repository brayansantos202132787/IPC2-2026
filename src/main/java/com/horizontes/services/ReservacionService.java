/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.config.AppConfig;
import com.horizontes.dao.ClienteDAO;
import com.horizontes.dao.PaqueteDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.dto.ReservacionDTO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Cliente;
import com.horizontes.models.Paquete;
import com.horizontes.models.Reservacion;
import com.horizontes.utils.FechaUtil;
import com.horizontes.utils.Validaciones;
 
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ReservacionService {
     private static final int ESTADO_PENDIENTE   = 1;
    private static final int ESTADO_CONFIRMADA  = 2;
    private static final int ESTADO_CANCELADA   = 3;
    private static final int ESTADO_COMPLETADA  = 4;
 
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final PaqueteDAO     paqueteDAO     = new PaqueteDAO();
    private final ClienteDAO     clienteDAO     = new ClienteDAO();
 
    // =================== CREAR ===================
    /**
     * Crea una reservación nueva en estado Pendiente.
     * Valida: paquete activo, capacidad disponible, pasajeros existentes,
     * fecha de viaje futura.
     */
    public Reservacion crear(ReservacionDTO dto, int idUsuario) {
        if (dto == null) throw new ValidationException("El cuerpo de la solicitud es requerido.");
        Validaciones.enteroPositivo(dto.idPaquete, "idPaquete");
        Validaciones.requerido(dto.fechaViaje, "fechaViaje");
        if (dto.dpisPasajeros == null || dto.dpisPasajeros.isEmpty()) {
            throw new ValidationException("Debe incluir al menos un pasajero.");
        }
 
        try {
            // Validar paquete
            Paquete paquete = paqueteDAO.buscarPorId(dto.idPaquete);
            if (paquete == null) throw new ApiException(404, "Paquete no encontrado.");
            if (!paquete.isActivo()) throw new ValidationException("El paquete seleccionado no está activo.");
 
            // Validar fecha
            LocalDate fechaViaje = FechaUtil.parsearIso(dto.fechaViaje);
            Validaciones.fechaViajeNoEnPasado(fechaViaje);
 
            // Validar capacidad
            int ocupados  = paqueteDAO.contarPasajerosFuturos(dto.idPaquete);
            int nuevos    = dto.dpisPasajeros.size();
            if (ocupados + nuevos > paquete.getCapacidad()) {
                throw new ValidationException(
                    "El paquete supera su capacidad. Disponibles: " +
                    (paquete.getCapacidad() - ocupados) + " lugar(es).");
            }
 
            // Resolver pasajeros
            List<Cliente> pasajeros = resolverPasajeros(dto.dpisPasajeros);
 
            // Calcular costo
            BigDecimal costoTotal = paquete.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(nuevos));
 
            // Construir entidad
            Reservacion r = new Reservacion();
            r.setNumeroReservacion(reservacionDAO.generarNumeroReservacion());
            r.setIdPaquete        (dto.idPaquete);
            r.setIdUsuario        (idUsuario);
            r.setFechaCreacion    (FechaUtil.hoy());
            r.setFechaViaje       (fechaViaje);
            r.setCantidadPasajeros(nuevos);
            r.setCostoTotal       (costoTotal);
            r.setIdEstado         (ESTADO_PENDIENTE);
 
            int idReservacion = reservacionDAO.insertar(r);
            r.setIdReservacion(idReservacion);
 
            // Registrar pasajeros
            for (Cliente p : pasajeros) {
                reservacionDAO.insertarPasajero(idReservacion, p.getIdCliente());
            }
 
            r.setPasajeros    (pasajeros);
            r.setNombrePaquete(paquete.getNombre());
            r.setNombreEstado ("Pendiente");
            return r;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al crear reservación: " + e.getMessage(), e);
        }
    }
 
    // =================== LISTAR ===================
    public List<Reservacion> listarTodas() {
        try {
            return enricher(reservacionDAO.listarTodas());
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar reservaciones: " + e.getMessage(), e);
        }
    }
 
    public List<Reservacion> listarDelDia() {
        try {
            return enricher(reservacionDAO.listarDelDia());
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar reservaciones del día: " + e.getMessage(), e);
        }
    }
 
    public List<Reservacion> listarPorCliente(int idCliente) {
        try {
            return enricher(reservacionDAO.listarPorCliente(idCliente));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar historial del cliente: " + e.getMessage(), e);
        }
    }
 
    public List<Reservacion> listarConFiltros(String desde, String hasta, Integer idEstado,
                                               int idDestino, int idPaquete) {
        try {
            LocalDate d = (desde != null && !desde.isBlank()) ? FechaUtil.parsearIso(desde) : null;
            LocalDate h = (hasta != null && !hasta.isBlank()) ? FechaUtil.parsearIso(hasta) : null;
            return enricher(reservacionDAO.listarConFiltros(d, h, idEstado, idDestino, idPaquete));
        } catch (SQLException e) {
            throw new ApiException(500, "Error al filtrar reservaciones: " + e.getMessage(), e);
        }
    }
 
    // =================== OBTENER POR ID / NÚMERO ===================
    public Reservacion obtenerPorId(int id) {
        try {
            Reservacion r = reservacionDAO.buscarPorId(id);
            if (r == null) throw new ApiException(404, "Reservación no encontrada.");
            r.setPasajeros(reservacionDAO.obtenerPasajeros(id));
            return r;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al obtener reservación: " + e.getMessage(), e);
        }
    }
 
    public Reservacion obtenerPorNumero(String numero) {
        try {
            Reservacion r = reservacionDAO.buscarPorNumero(numero);
            if (r == null) throw new ApiException(404, "Reservación no encontrada: " + numero);
            r.setPasajeros(reservacionDAO.obtenerPasajeros(r.getIdReservacion()));
            return r;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al obtener reservación: " + e.getMessage(), e);
        }
    }
 
    // =================== CAMBIAR ESTADO ===================
    public Reservacion marcarCompletada(int idReservacion) {
        return cambiarEstado(idReservacion, ESTADO_COMPLETADA, ESTADO_CONFIRMADA);
    }
 
    public Reservacion cambiarEstado(int idReservacion, int nuevoEstado, int estadoRequerido) {
        try {
            Reservacion r = reservacionDAO.buscarPorId(idReservacion);
            if (r == null) throw new ApiException(404, "Reservación no encontrada.");
            if (estadoRequerido > 0 && r.getIdEstado() != estadoRequerido) {
                throw new ValidationException(
                    "La reservación debe estar en estado requerido para esta operación.");
            }
            reservacionDAO.cambiarEstado(idReservacion, nuevoEstado);
            r.setIdEstado(nuevoEstado);
            return r;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al cambiar estado: " + e.getMessage(), e);
        }
    }
 
    // =================== AUXILIARES ===================
    private List<Cliente> resolverPasajeros(List<String> dpis) throws SQLException {
        List<Cliente> pasajeros = new ArrayList<>();
        for (String dpi : dpis) {
            Cliente c = clienteDAO.buscarPorDpi(dpi.trim());
            if (c == null) throw new ValidationException(
                "No existe cliente con DPI: " + dpi + ". Regístrelo primero.");
            pasajeros.add(c);
        }
        return pasajeros;
    }
 
   
    private List<Reservacion> enricher(List<Reservacion> lista) throws SQLException {
        for (Reservacion r : lista) {
            r.setPasajeros(reservacionDAO.obtenerPasajeros(r.getIdReservacion()));
        }
        return lista;
    }
}
