/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.dao.UsuarioDAO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Usuario;
import org.mindrot.jbcrypt.BCrypt;
 
import java.sql.SQLException;

/**
 *
 * @author braya
 */
public class AuthService {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
 
    
    public Usuario login(String nombre, String password) {
        if (nombre == null || nombre.isBlank())   throw new ValidationException("El nombre de usuario es requerido.");
        if (password == null || password.isBlank()) throw new ValidationException("La contraseña es requerida.");
 
        try {
            Usuario usuario = usuarioDAO.buscarPorNombre(nombre.trim());
 
            if (usuario == null) {
                throw new ApiException(401, "Credenciales inválidas.");
            }
            if (!usuario.isActivo()) {
                throw new ApiException(403, "El usuario está desactivado. Contacte al administrador.");
            }
            if (!BCrypt.checkpw(password, usuario.getPassword())) {
                throw new ApiException(401, "Credenciales inválidas.");
            }
 
            
            usuario.setPassword(null);
            return usuario;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al verificar credenciales: " + e.getMessage(), e);
        }
    }
 
   
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }
 
    
    public boolean verificarPassword(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }
}
