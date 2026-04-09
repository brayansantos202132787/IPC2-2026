/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;


import com.horizontes.exceptions.ApiException;
import com.horizontes.models.Cancelacion;
import com.horizontes.services.CancelacionService;
import com.horizontes.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
 
import java.io.IOException;
import java.util.List;

/**
 *
 * @author braya
 */
@WebServlet("/api/cancelaciones/*")
public class CancelacionServlet extends HttpServlet {
 
    private final CancelacionService cancelacionService = new CancelacionService();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if (path.equals("/") || path.equals("")) {
                String desde = req.getParameter("desde");
                String hasta = req.getParameter("hasta");
                List<Cancelacion> lista = cancelacionService.listarEnPeriodo(desde, hasta);
                JsonUtil.writeJson(resp, lista);
 
            } else if (path.startsWith("/simular/")) {
                int idReservacion = Integer.parseInt(path.substring(9));
                Cancelacion sim   = cancelacionService.simular(idReservacion);
                JsonUtil.writeJson(resp, sim);
 
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
            int idReservacion  = Integer.parseInt(path.replace("/", ""));
            Cancelacion result = cancelacionService.procesar(idReservacion);
            JsonUtil.writeJson(resp, 200, result);
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID de reservación inválido.");
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
}
