/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.google.gson.JsonObject;
import com.horizontes.dao.PagoDAO;
import com.horizontes.dao.ReservacionDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.models.Pago;
import com.horizontes.models.Reservacion;
import com.horizontes.services.PagoService;
import com.horizontes.utils.JsonUtil;
import com.horizontes.utils.PdfUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
 
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */

@WebServlet("/api/pagos/*")
public class PagoServlet extends HttpServlet {
 
    private final PagoService    pagoService    = new PagoService();
    private final ReservacionDAO reservacionDAO = new ReservacionDAO();
    private final PagoDAO        pagoDAO        = new PagoDAO();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if (path.matches("/\\d+/saldo")) {
                int idReservacion = Integer.parseInt(path.split("/")[1]);
                BigDecimal saldo  = pagoService.saldoPendiente(idReservacion);
                JsonObject obj    = new JsonObject();
                obj.addProperty("idReservacion", idReservacion);
                obj.addProperty("saldoPendiente", saldo);
                JsonUtil.writeJson(resp, obj);
 
            } else if (path.matches("/\\d+/comprobante")) {
                int idReservacion = Integer.parseInt(path.split("/")[1]);
                generarComprobante(idReservacion, resp);
 
            } else if (path.matches("/\\d+")) {
                int idReservacion = Integer.parseInt(path.replace("/", ""));
                List<Pago> pagos  = pagoService.listarPorReservacion(idReservacion);
                JsonUtil.writeJson(resp, pagos);
 
            } else {
                JsonUtil.writeError(resp, 404, "Ruta no encontrada.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            int idReservacion = Integer.parseInt(path.replace("/", ""));
            JsonObject body   = JsonUtil.bodyAsObject(req);
 
            BigDecimal monto    = body.get("monto").getAsBigDecimal();
            int        idMetodo = body.get("idMetodo").getAsInt();
            String     fecha    = body.has("fechaPago") ? body.get("fechaPago").getAsString() : null;
 
            Pago pago = pagoService.registrar(idReservacion, monto, idMetodo, fecha);
            JsonUtil.writeJson(resp, 201, pago);
 
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID de reservación inválido.");
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    // =================== COMPROBANTE PDF ===================
    private void generarComprobante(int idReservacion, HttpServletResponse resp) throws IOException {
        try {
            Reservacion   reservacion = reservacionDAO.buscarPorId(idReservacion);
            if (reservacion == null) { JsonUtil.writeError(resp, 404, "Reservación no encontrada."); return; }
 
            List<Pago>    pagos       = pagoDAO.listarPorReservacion(idReservacion);
            BigDecimal    totalPagado = pagoDAO.totalPagado(idReservacion);
 
            PdfUtil.generarComprobantePago(resp, reservacion, pagos, totalPagado);
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al generar comprobante: " + e.getMessage());
        }
    }
}
