/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;

import com.horizontes.dto.ClienteDTO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.models.Cliente;
import com.horizontes.services.ClienteService;
import com.horizontes.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
 
import java.io.IOException;
import java.util.List;

/**
 *
 * @author braya
 */
@WebServlet("/api/clientes/*")
public class ClienteServlet extends HttpServlet {
 
    private final ClienteService clienteService = new ClienteService();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // null | "/" | "/{id}" | "/dpi/{dpi}"
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Buscar por nombre o listar todos
                String nombre = req.getParameter("nombre");
                List<Cliente> lista = clienteService.buscarPorNombre(nombre);
                JsonUtil.writeJson(resp, lista);
 
            } else if (pathInfo.startsWith("/dpi/")) {
                String dpi = pathInfo.substring(5);
                JsonUtil.writeJson(resp, clienteService.obtenerPorDpi(dpi));
 
            } else {
                int id = parsearId(pathInfo, resp);
                if (id < 0) return;
                JsonUtil.writeJson(resp, clienteService.obtenerPorId(id));
            }
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ClienteDTO dto = JsonUtil.fromRequest(req, ClienteDTO.class);
            Cliente c = clienteService.registrar(dto);
            JsonUtil.writeJson(resp, 201, c);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            int id = parsearId(pathInfo, resp);
            if (id < 0) return;
            ClienteDTO dto = JsonUtil.fromRequest(req, ClienteDTO.class);
            Cliente c = clienteService.actualizar(id, dto);
            JsonUtil.writeJson(resp, c);
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    // =================== AUXILIAR ===================
    private int parsearId(String pathInfo, HttpServletResponse resp) throws IOException {
        try {
            return Integer.parseInt(pathInfo.replace("/", ""));
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "ID inválido.");
            return -1;
        }
    }
}
 
