/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.horizontes.dao.DestinoDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Destino;
import com.horizontes.utils.JsonUtil;
import com.horizontes.utils.Validaciones;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
 
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
@WebServlet("/api/destinos/*")
public class DestinoServlet extends HttpServlet {
 
    private final DestinoDAO destinoDAO = new DestinoDAO();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Destino> lista = destinoDAO.listarTodos();
                JsonUtil.writeJson(resp, lista);
            } else {
                int id = parsearId(pathInfo, resp);
                if (id < 0) return;
                Destino d = destinoDAO.buscarPorId(id);
                if (d == null) { JsonUtil.writeError(resp, 404, "Destino no encontrado."); return; }
                JsonUtil.writeJson(resp, d);
            }
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al obtener destinos: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Destino d = JsonUtil.fromRequest(req, Destino.class);
            Validaciones.requerido(d.getNombre(), "nombre");
            Validaciones.requerido(d.getPais(),   "pais");
            Validaciones.longitud (d.getNombre(), "nombre", 150);
            Validaciones.longitud (d.getPais(),   "pais",   100);
 
            if (destinoDAO.buscarPorNombre(d.getNombre()) != null) {
                throw new ValidationException("Ya existe un destino con ese nombre.");
            }
            int id = destinoDAO.insertar(d);
            d.setIdDestino(id);
            JsonUtil.writeJson(resp, 201, d);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al crear destino: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            int id = parsearId(pathInfo, resp);
            if (id < 0) return;
 
            if (destinoDAO.buscarPorId(id) == null) {
                JsonUtil.writeError(resp, 404, "Destino no encontrado."); return;
            }
 
            Destino d = JsonUtil.fromRequest(req, Destino.class);
            Validaciones.requerido(d.getNombre(), "nombre");
            Validaciones.requerido(d.getPais(),   "pais");
            d.setIdDestino(id);
            destinoDAO.actualizar(d);
            JsonUtil.writeJson(resp, d);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al actualizar destino: " + e.getMessage());
        }
    }
 
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            int id = parsearId(pathInfo, resp);
            if (id < 0) return;
 
            boolean eliminado = destinoDAO.eliminar(id);
            if (!eliminado) { JsonUtil.writeError(resp, 404, "Destino no encontrado."); return; }
            JsonUtil.writeSuccess(resp, "Destino eliminado.", id);
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al eliminar destino: " + e.getMessage());
        }
    }
 
    private int parsearId(String pathInfo, HttpServletResponse resp) throws IOException {
        try {
            return Integer.parseInt(pathInfo.replace("/", ""));
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
            return -1;
        }
    }
}