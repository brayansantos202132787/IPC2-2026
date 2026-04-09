/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.filters;

import com.horizontes.config.AppConfig;
import com.horizontes.models.Usuario;
import com.horizontes.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author braya
 */
public class RoleFilter implements Filter {
     private static final int ROL_ADMIN = AppConfig.ROL_ADMINISTRADOR;
 
    
    private static final Map<String, Set<Integer>> PERMISOS = new HashMap<>();
 
    static {
        // Área de Atención al Cliente
        PERMISOS.put("/api/clientes",      Set.of(1, 3));
        PERMISOS.put("/api/reservaciones", Set.of(1, 3));
        PERMISOS.put("/api/pagos",         Set.of(1, 3));
        PERMISOS.put("/api/cancelaciones", Set.of(1, 3));
 
        // Área de Operaciones
        PERMISOS.put("/api/destinos",      Set.of(2, 3));
        PERMISOS.put("/api/proveedores",   Set.of(2, 3));
        PERMISOS.put("/api/paquetes",      Set.of(2, 3));
 
        // Área Financiera / Administración
        PERMISOS.put("/api/reportes",      Set.of(3));
        PERMISOS.put("/api/carga",         Set.of(3));
        PERMISOS.put("/api/usuarios",      Set.of(3));
    }
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
 
        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;
 
        // Pre-flight ya fue manejado por AuthFilter
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
 
        // Rutas públicas ya fueron manejadas por AuthFilter
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            // AuthFilter ya respondió, no llegar aquí normalmente
            chain.doFilter(request, response);
            return;
        }
 
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        int     rol     = usuario.getIdRol();
 
        // Administrador: acceso total
        if (rol == ROL_ADMIN) {
            chain.doFilter(request, response);
            return;
        }
 
        // Determinar la ruta base
        String path = req.getServletPath() +
                (req.getPathInfo() != null ? req.getPathInfo() : "");
 
        for (Map.Entry<String, Set<Integer>> entry : PERMISOS.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                if (!entry.getValue().contains(rol)) {
                    JsonUtil.writeError(resp, 403,
                        "Acceso denegado. No tiene permisos para este recurso.");
                    return;
                }
                break;
            }
        }
 
        chain.doFilter(request, response);
    }
}
