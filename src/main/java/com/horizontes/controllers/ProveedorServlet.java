/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.horizontes.dao.ProveedorDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Proveedor;
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
@WebServlet("/api/proveedores/*")
public class ProveedorServlet extends HttpServlet {
 
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String tipoParam = req.getParameter("tipo");
                List<Proveedor> lista;
                if (tipoParam != null) {
                    int tipo = Integer.parseInt(tipoParam);
                    lista = proveedorDAO.listarPorTipo(tipo);
                } else {
                    lista = proveedorDAO.listarTodos();
                }
                JsonUtil.writeJson(resp, lista);
            } else {
                int id = parsearId(pathInfo, resp);
                if (id < 0) return;
                Proveedor p = proveedorDAO.buscarPorId(id);
                if (p == null) { JsonUtil.writeError(resp, 404, "Proveedor no encontrado."); return; }
                JsonUtil.writeJson(resp, p);
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "Parámetro 'tipo' debe ser un número.");
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al obtener proveedores: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Proveedor p = JsonUtil.fromRequest(req, Proveedor.class);
            Validaciones.requerido(p.getNombre(), "nombre");
            Validaciones.longitud (p.getNombre(), "nombre", 150);
            Validaciones.requerido(p.getPais(),   "pais");
            if (p.getIdTipo() < 1 || p.getIdTipo() > 5) throw new ValidationException("Tipo de proveedor inválido (1-5).");
 
            if (proveedorDAO.buscarPorNombre(p.getNombre()) != null) {
                throw new ValidationException("Ya existe un proveedor con ese nombre.");
            }
            int id = proveedorDAO.insertar(p);
            p.setIdProveedor(id);
            JsonUtil.writeJson(resp, 201, p);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al crear proveedor: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = parsearId(req.getPathInfo(), resp);
            if (id < 0) return;
            if (proveedorDAO.buscarPorId(id) == null) {
                JsonUtil.writeError(resp, 404, "Proveedor no encontrado."); return;
            }
            Proveedor p = JsonUtil.fromRequest(req, Proveedor.class);
            Validaciones.requerido(p.getNombre(), "nombre");
            p.setIdProveedor(id);
            proveedorDAO.actualizar(p);
            JsonUtil.writeJson(resp, p);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al actualizar proveedor: " + e.getMessage());
        }
    }
 
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int id = parsearId(req.getPathInfo(), resp);
            if (id < 0) return;
            boolean ok = proveedorDAO.eliminar(id);
            if (!ok) { JsonUtil.writeError(resp, 404, "Proveedor no encontrado."); return; }
            JsonUtil.writeSuccess(resp, "Proveedor eliminado.", id);
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al eliminar proveedor: " + e.getMessage());
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