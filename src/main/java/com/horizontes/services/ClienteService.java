/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.dao.ClienteDAO;
import com.horizontes.dto.ClienteDTO;
import com.horizontes.exceptions.ApiException;
import com.horizontes.exceptions.ValidationException;
import com.horizontes.models.Cliente;
import com.horizontes.utils.FechaUtil;
import com.horizontes.utils.Validaciones;
 
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class ClienteService {
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
 
    // =================== BUSCAR O REGISTRAR ===================
    /**
     * Si el cliente con ese DPI ya existe, lo devuelve.
     * Si no existe, lo registra y devuelve el nuevo.
     * Usado al inicio de una reservación.
     */
    public Cliente buscarORegistrar(ClienteDTO dto) {
        validarDTO(dto);
        try {
            Cliente existente = clienteDAO.buscarPorDpi(dto.dpi.trim());
            if (existente != null) return existente;
 
            Cliente nuevo = dtoAModelo(dto);
            int id = clienteDAO.insertar(nuevo);
            nuevo.setIdCliente(id);
            return nuevo;
 
        } catch (SQLException e) {
            throw new ApiException(500, "Error al registrar cliente: " + e.getMessage(), e);
        }
    }
 
    // =================== LISTAR ===================
    public List<Cliente> listarTodos() {
        try {
            return clienteDAO.listarTodos();
        } catch (SQLException e) {
            throw new ApiException(500, "Error al listar clientes: " + e.getMessage(), e);
        }
    }
 
    public List<Cliente> buscarPorNombre(String fragmento) {
        if (fragmento == null || fragmento.isBlank()) return listarTodos();
        try {
            return clienteDAO.buscarPorNombre(fragmento.trim());
        } catch (SQLException e) {
            throw new ApiException(500, "Error al buscar clientes: " + e.getMessage(), e);
        }
    }
 
    // =================== OBTENER POR ID / DPI ===================
    public Cliente obtenerPorId(int id) {
        try {
            Cliente c = clienteDAO.buscarPorId(id);
            if (c == null) throw new ApiException(404, "Cliente no encontrado con id: " + id);
            return c;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al obtener cliente: " + e.getMessage(), e);
        }
    }
 
    public Cliente obtenerPorDpi(String dpi) {
        try {
            Cliente c = clienteDAO.buscarPorDpi(dpi);
            if (c == null) throw new ApiException(404, "Cliente no encontrado con DPI: " + dpi);
            return c;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al obtener cliente: " + e.getMessage(), e);
        }
    }
 
    // =================== REGISTRAR ===================
    public Cliente registrar(ClienteDTO dto) {
        validarDTO(dto);
        try {
            if (clienteDAO.buscarPorDpi(dto.dpi.trim()) != null) {
                throw new ValidationException("Ya existe un cliente con el DPI: " + dto.dpi);
            }
            Cliente c = dtoAModelo(dto);
            int id = clienteDAO.insertar(c);
            c.setIdCliente(id);
            return c;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al registrar cliente: " + e.getMessage(), e);
        }
    }
 
    // =================== ACTUALIZAR ===================
    public Cliente actualizar(int id, ClienteDTO dto) {
        validarDTO(dto);
        try {
            Cliente existente = clienteDAO.buscarPorId(id);
            if (existente == null) throw new ApiException(404, "Cliente no encontrado con id: " + id);
 
            existente.setNombre      (dto.nombre.trim());
            existente.setFechaNac    (FechaUtil.parsearIso(dto.fechaNac));
            existente.setTelefono    (dto.telefono);
            existente.setEmail       (dto.email);
            existente.setNacionalidad(dto.nacionalidad);
 
            clienteDAO.actualizar(existente);
            return existente;
        } catch (SQLException e) {
            throw new ApiException(500, "Error al actualizar cliente: " + e.getMessage(), e);
        }
    }
 
    // =================== VALIDACIONES INTERNAS ===================
    private void validarDTO(ClienteDTO dto) {
        if (dto == null) throw new ValidationException("El cuerpo de la solicitud es requerido.");
        Validaciones.dpi   (dto.dpi);
        Validaciones.requerido(dto.nombre,   "nombre");
        Validaciones.longitud (dto.nombre,   "nombre", 200);
        Validaciones.requerido(dto.fechaNac, "fechaNac");
        Validaciones.longitud (dto.telefono, "telefono", 20);
        Validaciones.longitud (dto.email,    "email",    150);
    }
 
    private Cliente dtoAModelo(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setDpi         (dto.dpi.trim());
        c.setNombre      (dto.nombre.trim());
        c.setFechaNac    (FechaUtil.parsearIso(dto.fechaNac));
        c.setTelefono    (dto.telefono);
        c.setEmail       (dto.email);
        c.setNacionalidad(dto.nacionalidad);
        return c;
    }
}
