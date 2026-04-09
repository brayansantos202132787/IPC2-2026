/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.horizontes.dto.ReservacionDTO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.models.Reservacion;
import com.horizontes.models.Usuario;
import com.horizontes.services.ReservacionService;
import com.horizontes.utils.JsonUtil;

 
import java.io.IOException;
import java.util.List;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;

/**
 *
 * @author braya
 */
@WebServlet("/api/reservaciones/*")
public class ReservacionServlet extends HttpServlet {
 
    private final ReservacionService reservacionService = new ReservacionService();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if (path.equals("/") || path.equals("")) {
                String desde    = req.getParameter("desde");
                String hasta    = req.getParameter("hasta");
                String estadoP  = req.getParameter("idEstado");
                String destinoP = req.getParameter("idDestino");
                String paqueteP = req.getParameter("idPaquete");
 
                Integer idEstado  = estadoP  != null ? Integer.parseInt(estadoP)  : null;
                int     idDestino = destinoP != null ? Integer.parseInt(destinoP) : -1;
                int     idPaquete = paqueteP != null ? Integer.parseInt(paqueteP) : -1;
 
                List<Reservacion> lista = reservacionService.listarConFiltros(
                        desde, hasta, idEstado, idDestino, idPaquete);
                JsonUtil.writeJson(resp, lista);
 
            } else if (path.equals("/hoy")) {
                JsonUtil.writeJson(resp, reservacionService.listarDelDia());
 
            } else if (path.startsWith("/num/")) {
                String numero = path.substring(5);
                JsonUtil.writeJson(resp, reservacionService.obtenerPorNumero(numero));
 
            } else if (path.startsWith("/cliente/")) {
                int idCliente = Integer.parseInt(path.substring(9));
                JsonUtil.writeJson(resp, reservacionService.listarPorCliente(idCliente));
 
            } else {
                int id = Integer.parseInt(path.replace("/", ""));
                JsonUtil.writeJson(resp, reservacionService.obtenerPorId(id));
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "Parámetro numérico inválido.");
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Obtener el id del agente desde la sesión
            HttpSession session  = req.getSession(false);
            Usuario     usuario  = (Usuario) session.getAttribute("usuario");
            int         idAgente = usuario.getIdUsuario();
 
            ReservacionDTO dto  = JsonUtil.fromRequest(req, ReservacionDTO.class);
            Reservacion    r    = reservacionService.crear(dto, idAgente);
            JsonUtil.writeJson(resp, 201, r);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            // PUT /api/reservaciones/{id}/completar
            if (path.matches("/\\d+/completar")) {
                int id = Integer.parseInt(path.split("/")[1]);
                Reservacion r = reservacionService.marcarCompletada(id);
                JsonUtil.writeJson(resp, r);
            } else {
                JsonUtil.writeError(resp, 404, "Ruta no encontrada.");
            }
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
}