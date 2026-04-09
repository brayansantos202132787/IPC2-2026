/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.controllers;
 
import com.google.gson.JsonObject;
import com.horizontes.exceptions.ApiException;
import com.horizontes.models.Usuario;
import com.horizontes.services.AuthService;
import com.horizontes.utils.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
 
import java.io.IOException;

/**
 *
 * @author braya
 */

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
 
    private final AuthService authService = new AuthService();
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
 
        try {
            switch (path) {
                case "/login"  -> login(req, resp);
                case "/logout" -> logout(req, resp);
                default        -> JsonUtil.writeError(resp, 404, "Ruta no encontrada: " + path);
            }
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, 500, "Error interno: " + e.getMessage());
        }
    }
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() != null ? req.getPathInfo() : "/";
        try {
            if ("/me".equals(path)) {
                me(req, resp);
            } else {
                JsonUtil.writeError(resp, 404, "Ruta no encontrada.");
            }
        } catch (ApiException e) {
            JsonUtil.writeError(resp, e.getHttpStatus(), e.getMessage());
        }
    }
 
    // =================== LOGIN ===================
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = JsonUtil.bodyAsObject(req);
 
        if (!body.has("nombre") || !body.has("password")) {
            JsonUtil.writeError(resp, 400, "Se requieren 'nombre' y 'password'.");
            return;
        }
 
        String nombre   = body.get("nombre").getAsString();
        String password = body.get("password").getAsString();
 
        Usuario usuario = authService.login(nombre, password);
 
        // Crear sesión
        HttpSession session = req.getSession(true);
        session.setAttribute("usuario", usuario);
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("rol", usuario.getIdRol());
 
        // Respuesta (sin password)
        JsonObject respBody = new JsonObject();
        respBody.addProperty("message",    "Login exitoso.");
        respBody.addProperty("idUsuario",  usuario.getIdUsuario());
        respBody.addProperty("nombre",     usuario.getNombre());
        respBody.addProperty("idRol",      usuario.getIdRol());
        respBody.addProperty("nombreRol",  usuario.getNombreRol());
 
        JsonUtil.writeJson(resp, respBody);
    }
 
    // =================== LOGOUT ===================
    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        JsonUtil.writeSuccess(resp, "Sesión cerrada correctamente.", null);
    }
 
    // =================== ME ===================
    private void me(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            JsonUtil.writeError(resp, 401, "No autenticado.");
            return;
        }
        Usuario u = (Usuario) session.getAttribute("usuario");
        JsonObject respBody = new JsonObject();
        respBody.addProperty("idUsuario",  u.getIdUsuario());
        respBody.addProperty("nombre",     u.getNombre());
        respBody.addProperty("idRol",      u.getIdRol());
        respBody.addProperty("nombreRol",  u.getNombreRol());
        JsonUtil.writeJson(resp, respBody);
    }
}
