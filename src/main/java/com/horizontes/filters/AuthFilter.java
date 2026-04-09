/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.filters;

import com.horizontes.config.AppConfig;
import com.horizontes.models.Usuario;
import com.horizontes.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
 
import java.io.IOException;

/**
 *
 * @author braya
 */
public class AuthFilter implements Filter {
    
    private static final String[] RUTAS_PUBLICAS = {
        "/api/auth/login",
        "/api/auth/logout"
    };
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
 
        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;
 
        // -------- CORS --------
        resp.setHeader("Access-Control-Allow-Origin",      AppConfig.CORS_ORIGIN);
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods",     "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        resp.setHeader("Access-Control-Allow-Headers",     "Content-Type, Authorization, X-Requested-With");
        resp.setHeader("Access-Control-Expose-Headers",    "Content-Disposition");
 
        // -------- Pre-flight OPTIONS --------
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }
 
        // -------- Rutas públicas --------
        String path = req.getServletPath() +
                (req.getPathInfo() != null ? req.getPathInfo() : "");
 
        for (String publica : RUTAS_PUBLICAS) {
            if (path.startsWith(publica)) {
                chain.doFilter(request, response);
                return;
            }
        }
 
       
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            JsonUtil.writeError(resp, 401, "No autenticado. Inicie sesión primero.");
            return;
        }
 
        // Refrescar timeout
        session.setMaxInactiveInterval(AppConfig.SESSION_TIMEOUT_SECONDS);
 
        chain.doFilter(request, response);
    }
}
