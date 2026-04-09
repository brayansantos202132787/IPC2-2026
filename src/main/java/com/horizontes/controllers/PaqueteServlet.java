/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.google.gson.JsonObject;
import com.horizontes.config.AppConfig;
import com.horizontes.dao.PaqueteDAO;
import com.horizontes.dao.ServicioPaqueteDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Paquete;
import com.horizontes.models.ServicioPaquete;
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
@WebServlet("/api/paquetes/*")
public class PaqueteServlet extends HttpServlet {
 
    private final PaqueteDAO        paqueteDAO  = new PaqueteDAO();
    private final ServicioPaqueteDAO servicioDAO = new ServicioPaqueteDAO();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if (path.equals("/") || path.equals("")) {
                String destinoParam = req.getParameter("destino");
                List<Paquete> lista = destinoParam != null
                        ? paqueteDAO.listarPorDestino(Integer.parseInt(destinoParam))
                        : paqueteDAO.listarActivos();
                // Inyectar servicios en cada paquete
                for (Paquete p : lista) {
                    p.setServicios(servicioDAO.listarPorPaquete(p.getIdPaquete()));
                }
                JsonUtil.writeJson(resp, lista);
 
            } else if (path.equals("/todos")) {
                List<Paquete> lista = paqueteDAO.listarTodos();
                for (Paquete p : lista) {
                    p.setServicios(servicioDAO.listarPorPaquete(p.getIdPaquete()));
                }
                JsonUtil.writeJson(resp, lista);
 
            } else if (path.matches("/\\d+/alerta")) {
                int id = Integer.parseInt(path.split("/")[1]);
                Paquete p = paqueteDAO.buscarPorId(id);
                if (p == null) { JsonUtil.writeError(resp, 404, "Paquete no encontrado."); return; }
                int ocupados   = paqueteDAO.contarPasajerosFuturos(id);
                double porcentaje = (double) ocupados / p.getCapacidad();
                JsonObject obj = new JsonObject();
                obj.addProperty("idPaquete",   id);
                obj.addProperty("nombre",      p.getNombre());
                obj.addProperty("capacidad",   p.getCapacidad());
                obj.addProperty("ocupados",    ocupados);
                obj.addProperty("porcentaje",  Math.round(porcentaje * 100));
                obj.addProperty("altaDemanda", porcentaje >= AppConfig.ALERTA_OCUPACION_PORCIENTO);
                JsonUtil.writeJson(resp, obj);
 
            } else {
                String[] partes = path.split("/");
                int id = Integer.parseInt(partes[1]);
                Paquete p = paqueteDAO.buscarPorId(id);
                if (p == null) { JsonUtil.writeError(resp, 404, "Paquete no encontrado."); return; }
                p.setServicios(servicioDAO.listarPorPaquete(id));
                JsonUtil.writeJson(resp, p);
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al obtener paquetes: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            // Agregar servicio a paquete: POST /api/paquetes/{id}/servicios
            if (path.matches("/\\d+/servicios")) {
                int idPaquete = Integer.parseInt(path.split("/")[1]);
                if (paqueteDAO.buscarPorId(idPaquete) == null) {
                    JsonUtil.writeError(resp, 404, "Paquete no encontrado."); return;
                }
                ServicioPaquete sp = JsonUtil.fromRequest(req, ServicioPaquete.class);
                Validaciones.requerido(sp.getDescripcion(), "descripcion");
                Validaciones.noNulo  (sp.getCostoProveedor(), "costoProveedor");
                sp.setIdPaquete(idPaquete);
                int id = servicioDAO.insertar(sp);
                sp.setIdServicio(id);
                JsonUtil.writeJson(resp, 201, sp);
 
            } else {
                // Crear paquete: POST /api/paquetes
                Paquete p = JsonUtil.fromRequest(req, Paquete.class);
                Validaciones.requerido    (p.getNombre(), "nombre");
                Validaciones.enteroPositivo(p.getIdDestino(), "idDestino");
                Validaciones.enteroPositivo(p.getDuracionDias(), "duracionDias");
                Validaciones.positivo      (p.getPrecioVenta(), "precioVenta");
                Validaciones.enteroPositivo(p.getCapacidad(), "capacidad");
 
                if (paqueteDAO.buscarPorNombre(p.getNombre()) != null) {
                    throw new ValidationException("Ya existe un paquete con ese nombre.");
                }
                int id = paqueteDAO.insertar(p);
                p.setIdPaquete(id);
                JsonUtil.writeJson(resp, 201, p);
            }
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            int id = Integer.parseInt(path.replace("/", ""));
            if (paqueteDAO.buscarPorId(id) == null) {
                JsonUtil.writeError(resp, 404, "Paquete no encontrado."); return;
            }
            Paquete p = JsonUtil.fromRequest(req, Paquete.class);
            Validaciones.requerido(p.getNombre(), "nombre");
            p.setIdPaquete(id);
            paqueteDAO.actualizar(p);
            p.setServicios(servicioDAO.listarPorPaquete(id));
            JsonUtil.writeJson(resp, p);
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al actualizar paquete: " + e.getMessage());
        }
    }
 
    /** PATCH /api/paquetes/{id}/estado  body: { "activo": true/false } */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
 
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if (!path.matches("/\\d+/estado")) {
                JsonUtil.writeError(resp, 404, "Ruta no encontrada."); return;
            }
            int idPaquete = Integer.parseInt(path.split("/")[1]);
            JsonObject body = JsonUtil.bodyAsObject(req);
            boolean activo = body.get("activo").getAsBoolean();
            paqueteDAO.cambiarEstado(idPaquete, activo);
            JsonUtil.writeSuccess(resp, "Estado actualizado.", idPaquete);
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error: " + e.getMessage());
        }
    }
 
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            // DELETE /api/paquetes/servicios/{idServicio}
            if (path.startsWith("/servicios/")) {
                int idServicio = Integer.parseInt(path.replace("/servicios/", ""));
                boolean ok = servicioDAO.eliminar(idServicio);
                if (!ok) { JsonUtil.writeError(resp, 404, "Servicio no encontrado."); return; }
                JsonUtil.writeSuccess(resp, "Servicio eliminado.", idServicio);
            } else {
                JsonUtil.writeError(resp, 405, "Método no permitido en esta ruta.");
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
        } catch (SQLException e) {
            JsonUtil.writeError(resp, 500, "Error al eliminar servicio: " + e.getMessage());
        }
    }
}
