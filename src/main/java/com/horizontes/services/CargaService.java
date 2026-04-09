/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.services;

import com.horizontes.dao.*;
import com.horizontes.models.*;
import com.horizontes.utils.FechaUtil;
import org.mindrot.jbcrypt.BCrypt;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author braya
 */
public class CargaService {
    
    private final UsuarioDAO        usuarioDAO        = new UsuarioDAO();
    private final DestinoDAO        destinoDAO        = new DestinoDAO();
    private final ProveedorDAO      proveedorDAO      = new ProveedorDAO();
    private final PaqueteDAO        paqueteDAO        = new PaqueteDAO();
    private final ServicioPaqueteDAO servicioDAO      = new ServicioPaqueteDAO();
    private final ClienteDAO        clienteDAO        = new ClienteDAO();
    private final ReservacionDAO    reservacionDAO    = new ReservacionDAO();
    private final PagoDAO           pagoDAO           = new PagoDAO();
 
    // Resultado de la carga
    public static class ResultadoCarga {
        public int total      = 0;
        public int exitosos   = 0;
        public int errores    = 0;
        public List<ErrorCarga> listaErrores = new ArrayList<>();
    }
 
    public static class ErrorCarga {
        public int    linea;
        public String tipo;         // FORMATO | LOGICO
        public String descripcion;
 
        public ErrorCarga(int linea, String tipo, String descripcion) {
            this.linea       = linea;
            this.tipo        = tipo;
            this.descripcion = descripcion;
        }
    }
 
    // =================== PROCESAR ARCHIVO ===================
    public ResultadoCarga procesar(InputStream is) {
        ResultadoCarga resultado = new ResultadoCarga();
 
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
 
            String linea;
            int numeroLinea = 0;
 
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
 
                // Ignorar líneas vacías o comentarios
                if (linea.isEmpty() || linea.startsWith("#")) continue;
 
                resultado.total++;
 
                try {
                    procesarLinea(linea, numeroLinea);
                    resultado.exitosos++;
                } catch (FormatoException e) {
                    resultado.errores++;
                    resultado.listaErrores.add(
                        new ErrorCarga(numeroLinea, "FORMATO", e.getMessage()));
                } catch (LogicoException e) {
                    resultado.errores++;
                    resultado.listaErrores.add(
                        new ErrorCarga(numeroLinea, "LOGICO", e.getMessage()));
                } catch (Exception e) {
                    resultado.errores++;
                    resultado.listaErrores.add(
                        new ErrorCarga(numeroLinea, "LOGICO", "Error inesperado: " + e.getMessage()));
                }
            }
 
        } catch (Exception e) {
            resultado.listaErrores.add(new ErrorCarga(0, "FORMATO", "Error al leer el archivo: " + e.getMessage()));
        }
 
        return resultado;
    }
 
    // =================== DESPACHAR INSTRUCCIÓN ===================
    private void procesarLinea(String linea, int numeroLinea) throws Exception {
        if      (linea.startsWith("USUARIO("))          procesarUsuario(linea);
        else if (linea.startsWith("DESTINO("))          procesarDestino(linea);
        else if (linea.startsWith("PROVEEDOR("))        procesarProveedor(linea);
        else if (linea.startsWith("PAQUETE("))          procesarPaquete(linea);
        else if (linea.startsWith("SERVICIO_PAQUETE(")) procesarServicioPaquete(linea);
        else if (linea.startsWith("CLIENTE("))          procesarCliente(linea);
        else if (linea.startsWith("RESERVACION("))      procesarReservacion(linea);
        else if (linea.startsWith("PAGO("))             procesarPago(linea);
        else throw new FormatoException("Instrucción desconocida: " + linea.split("\\(")[0]);
    }
 
    // =================== USUARIO ===================
    // USUARIO("jperez","miPass123",1)
    private void procesarUsuario(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 3) throw new FormatoException("USUARIO requiere 3 parámetros.");
 
        String nombre   = args.get(0);
        String password = args.get(1);
        int    tipo;
 
        try { tipo = Integer.parseInt(args.get(2)); }
        catch (NumberFormatException e) { throw new FormatoException("USUARIO: tipo debe ser entero (1-3)."); }
 
        if (nombre.isBlank())    throw new FormatoException("USUARIO: nombre no puede estar vacío.");
        if (password.length() < 6) throw new FormatoException("USUARIO: password debe tener mínimo 6 caracteres.");
        if (tipo < 1 || tipo > 3)  throw new FormatoException("USUARIO: tipo debe ser 1, 2 o 3.");
 
        if (usuarioDAO.existeNombre(nombre)) throw new LogicoException("USUARIO: nombre '" + nombre + "' ya existe.");
 
        Usuario u = new Usuario();
        u.setNombre  (nombre);
        u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
        u.setIdRol   (tipo);
        usuarioDAO.insertar(u);
    }
 
    // =================== DESTINO ===================
    // DESTINO("Cancún","México","Playas del Caribe mexicano")
    private void procesarDestino(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() < 2) throw new FormatoException("DESTINO requiere al menos 2 parámetros.");
 
        String nombre      = args.get(0);
        String pais        = args.get(1);
        String descripcion = args.size() > 2 ? args.get(2) : "";
 
        if (nombre.isBlank()) throw new FormatoException("DESTINO: nombre no puede estar vacío.");
        if (pais.isBlank())   throw new FormatoException("DESTINO: pais no puede estar vacío.");
        if (destinoDAO.buscarPorNombre(nombre) != null) throw new LogicoException("DESTINO: '" + nombre + "' ya existe.");
 
        Destino d = new Destino();
        d.setNombre     (nombre);
        d.setPais       (pais);
        d.setDescripcion(descripcion);
        destinoDAO.insertar(d);
    }
 
    // =================== PROVEEDOR ===================
    // PROVEEDOR("TACA Airlines",1,"Guatemala")
    private void procesarProveedor(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 3) throw new FormatoException("PROVEEDOR requiere 3 parámetros.");
 
        String nombre = args.get(0);
        int    tipo;
        String pais   = args.get(2);
 
        try { tipo = Integer.parseInt(args.get(1)); }
        catch (NumberFormatException e) { throw new FormatoException("PROVEEDOR: tipo debe ser entero (1-5)."); }
 
        if (nombre.isBlank()) throw new FormatoException("PROVEEDOR: nombre no puede estar vacío.");
        if (tipo < 1 || tipo > 5) throw new FormatoException("PROVEEDOR: tipo debe ser 1-5.");
        if (proveedorDAO.buscarPorNombre(nombre) != null) throw new LogicoException("PROVEEDOR: '" + nombre + "' ya existe.");
 
        Proveedor p = new Proveedor();
        p.setNombre  (nombre);
        p.setIdTipo  (tipo);
        p.setPais    (pais);
        proveedorDAO.insertar(p);
    }
 
    // =================== PAQUETE ===================
    // PAQUETE("Caribe Mágico 7 noches","Cancun",7,18500.00,20)
    private void procesarPaquete(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 5) throw new FormatoException("PAQUETE requiere 5 parámetros.");
 
        String nombre       = args.get(0);
        String nombreDestino= args.get(1);
        int    duracion;
        BigDecimal precio;
        int    capacidad;
 
        try { duracion  = Integer.parseInt(args.get(2)); }
        catch (NumberFormatException e) { throw new FormatoException("PAQUETE: duracion debe ser entero."); }
 
        try { precio = new BigDecimal(args.get(3)); }
        catch (NumberFormatException e) { throw new FormatoException("PAQUETE: precio inválido."); }
 
        try { capacidad = Integer.parseInt(args.get(4)); }
        catch (NumberFormatException e) { throw new FormatoException("PAQUETE: capacidad debe ser entero."); }
 
        if (duracion  <= 0) throw new FormatoException("PAQUETE: duracion debe ser positiva.");
        if (precio.compareTo(BigDecimal.ZERO) <= 0) throw new FormatoException("PAQUETE: precio debe ser positivo.");
        if (capacidad <= 0) throw new FormatoException("PAQUETE: capacidad debe ser positiva.");
 
        Destino destino = destinoDAO.buscarPorNombre(nombreDestino);
        if (destino == null) throw new LogicoException("PAQUETE: destino '" + nombreDestino + "' no existe.");
        if (paqueteDAO.buscarPorNombre(nombre) != null) throw new LogicoException("PAQUETE: '" + nombre + "' ya existe.");
 
        Paquete paq = new Paquete();
        paq.setNombre      (nombre);
        paq.setIdDestino   (destino.getIdDestino());
        paq.setDuracionDias(duracion);
        paq.setPrecioVenta (precio);
        paq.setCapacidad   (capacidad);
        paqueteDAO.insertar(paq);
    }
 
    // =================== SERVICIO_PAQUETE ===================
    // SERVICIO_PAQUETE("Caribe Magico 7 noches","TACA Airlines","Vuelo redondo GUA-CUN",5200.00)
    private void procesarServicioPaquete(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 4) throw new FormatoException("SERVICIO_PAQUETE requiere 4 parámetros.");
 
        String     nombrePaquete   = args.get(0);
        String     nombreProveedor = args.get(1);
        String     descripcion     = args.get(2);
        BigDecimal costo;
 
        try { costo = new BigDecimal(args.get(3)); }
        catch (NumberFormatException e) { throw new FormatoException("SERVICIO_PAQUETE: costo inválido."); }
 
        if (costo.compareTo(BigDecimal.ZERO) < 0) throw new FormatoException("SERVICIO_PAQUETE: costo no puede ser negativo.");
 
        Paquete  paquete  = paqueteDAO.buscarPorNombre(nombrePaquete);
        if (paquete  == null) throw new LogicoException("SERVICIO_PAQUETE: paquete '"  + nombrePaquete  + "' no existe.");
 
        Proveedor proveedor = proveedorDAO.buscarPorNombre(nombreProveedor);
        if (proveedor == null) throw new LogicoException("SERVICIO_PAQUETE: proveedor '" + nombreProveedor + "' no existe.");
 
        ServicioPaquete sp = new ServicioPaquete();
        sp.setIdPaquete     (paquete.getIdPaquete());
        sp.setIdProveedor   (proveedor.getIdProveedor());
        sp.setDescripcion   (descripcion);
        sp.setCostoProveedor(costo);
        servicioDAO.insertar(sp);
    }
 
    // =================== CLIENTE ===================
    // CLIENTE("1234567890101","Maria Lopez","15/03/1990","55551234","mlopez@mail.com","Guatemalteca")
    private void procesarCliente(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 6) throw new FormatoException("CLIENTE requiere 6 parámetros.");
 
        String dpi          = args.get(0);
        String nombre       = args.get(1);
        String fechaStr     = args.get(2);
        String telefono     = args.get(3);
        String email        = args.get(4);
        String nacionalidad = args.get(5);
 
        if (dpi.isBlank())    throw new FormatoException("CLIENTE: dpi no puede estar vacío.");
        if (nombre.isBlank()) throw new FormatoException("CLIENTE: nombre no puede estar vacío.");
 
        java.time.LocalDate fechaNac;
        try { fechaNac = FechaUtil.parsearArchivoFecha(fechaStr); }
        catch (IllegalArgumentException e) { throw new FormatoException("CLIENTE: " + e.getMessage()); }
 
        if (clienteDAO.buscarPorDpi(dpi) != null) throw new LogicoException("CLIENTE: DPI '" + dpi + "' ya existe.");
 
        Cliente c = new Cliente();
        c.setDpi         (dpi);
        c.setNombre      (nombre);
        c.setFechaNac    (fechaNac);
        c.setTelefono    (telefono);
        c.setEmail       (email);
        c.setNacionalidad(nacionalidad);
        clienteDAO.insertar(c);
    }
 
    // =================== RESERVACION ===================
    // RESERVACION("Caribe Magico 7 noches",jperez,"10/07/2025","1234567890101|9876543210101")
    private void procesarReservacion(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 4) throw new FormatoException("RESERVACION requiere 4 parámetros.");
 
        String   nombrePaquete  = args.get(0);
        String   nombreUsuario  = args.get(1);
        String   fechaViajeStr  = args.get(2);
        String[] dpis           = args.get(3).split("\\|");
 
        Paquete paquete = paqueteDAO.buscarPorNombre(nombrePaquete);
        if (paquete == null) throw new LogicoException("RESERVACION: paquete '" + nombrePaquete + "' no existe.");
 
        Usuario usuario = usuarioDAO.buscarPorNombre(nombreUsuario);
        if (usuario == null) throw new LogicoException("RESERVACION: usuario '" + nombreUsuario + "' no existe.");
 
        java.time.LocalDate fechaViaje;
        try { fechaViaje = FechaUtil.parsearArchivoFecha(fechaViajeStr); }
        catch (IllegalArgumentException e) { throw new FormatoException("RESERVACION: " + e.getMessage()); }
 
        List<Cliente> pasajeros = new ArrayList<>();
        for (String dpi : dpis) {
            Cliente c = clienteDAO.buscarPorDpi(dpi.trim());
            if (c == null) throw new LogicoException("RESERVACION: cliente con DPI '" + dpi.trim() + "' no existe.");
            pasajeros.add(c);
        }
 
        BigDecimal costoTotal = paquete.getPrecioVenta()
                .multiply(BigDecimal.valueOf(pasajeros.size()));
 
        Reservacion r = new Reservacion();
        r.setNumeroReservacion(reservacionDAO.generarNumeroReservacion());
        r.setIdPaquete        (paquete.getIdPaquete());
        r.setIdUsuario        (usuario.getIdUsuario());
        r.setFechaCreacion    (FechaUtil.hoy());
        r.setFechaViaje       (fechaViaje);
        r.setCantidadPasajeros(pasajeros.size());
        r.setCostoTotal       (costoTotal);
        r.setIdEstado         (1); // Pendiente
 
        int idReservacion = reservacionDAO.insertar(r);
        for (Cliente p : pasajeros) {
            reservacionDAO.insertarPasajero(idReservacion, p.getIdCliente());
        }
    }
 
    // =================== PAGO ===================
    // PAGO("RES-00001",18500.00,1,"05/06/2025")
    private void procesarPago(String linea) throws Exception {
        List<String> args = extraerArgs(linea);
        if (args.size() != 4) throw new FormatoException("PAGO requiere 4 parámetros.");
 
        String     numeroRes = args.get(0);
        BigDecimal monto;
        int        metodo;
        String     fechaStr  = args.get(3);
 
        try { monto  = new BigDecimal(args.get(1)); }
        catch (NumberFormatException e) { throw new FormatoException("PAGO: monto inválido."); }
 
        try { metodo = Integer.parseInt(args.get(2)); }
        catch (NumberFormatException e) { throw new FormatoException("PAGO: metodo debe ser entero (1-3)."); }
 
        if (monto.compareTo(BigDecimal.ZERO) <= 0) throw new FormatoException("PAGO: monto debe ser positivo.");
        if (metodo < 1 || metodo > 3) throw new FormatoException("PAGO: metodo debe ser 1, 2 o 3.");
 
        java.time.LocalDate fechaPago;
        try { fechaPago = FechaUtil.parsearArchivoFecha(fechaStr); }
        catch (IllegalArgumentException e) { throw new FormatoException("PAGO: " + e.getMessage()); }
 
        Reservacion reservacion = reservacionDAO.buscarPorNumero(numeroRes);
        if (reservacion == null) throw new LogicoException("PAGO: reservación '" + numeroRes + "' no existe.");
 
        Pago pago = new Pago();
        pago.setIdReservacion(reservacion.getIdReservacion());
        pago.setMonto        (monto);
        pago.setIdMetodo     (metodo);
        pago.setFechaPago    (fechaPago);
        pagoDAO.insertar(pago);
 
        // Verificar si el pago completa la reservación
        BigDecimal totalPagado = pagoDAO.totalPagado(reservacion.getIdReservacion());
        if (totalPagado.compareTo(reservacion.getCostoTotal()) >= 0) {
            reservacionDAO.cambiarEstado(reservacion.getIdReservacion(), 2); // Confirmada
        }
    }
 
    // =================== PARSER DE ARGUMENTOS ===================
    /**
     * Extrae los argumentos de una instrucción como lista de Strings.
     * Maneja strings entre comillas y valores numéricos.
     * Ejemplo: USUARIO("juan","pass123",1) → ["juan","pass123","1"]
     */
    private List<String> extraerArgs(String linea) throws FormatoException {
        int inicio = linea.indexOf('(');
        int fin    = linea.lastIndexOf(')');
        if (inicio < 0 || fin < 0) throw new FormatoException("Formato inválido: falta ( o ).");
 
        String contenido = linea.substring(inicio + 1, fin).trim();
        List<String> args = new ArrayList<>();
 
        // Parser manual para respetar comillas
        int    i    = 0;
        int    len  = contenido.length();
        while (i < len) {
            // Saltar espacios y comas
            while (i < len && (contenido.charAt(i) == ' ' || contenido.charAt(i) == ',')) i++;
            if (i >= len) break;
 
            if (contenido.charAt(i) == '"') {
                // Argumento entre comillas
                i++; // saltar comilla inicial
                StringBuilder sb = new StringBuilder();
                while (i < len && contenido.charAt(i) != '"') {
                    sb.append(contenido.charAt(i++));
                }
                if (i < len) i++; // saltar comilla final
                args.add(sb.toString());
            } else {
                // Argumento sin comillas (número, nombre de usuario sin comillas)
                StringBuilder sb = new StringBuilder();
                while (i < len && contenido.charAt(i) != ',') {
                    sb.append(contenido.charAt(i++));
                }
                args.add(sb.toString().trim());
            }
        }
        return args;
    }
 
    
    private static class FormatoException extends Exception {
        FormatoException(String msg) { super(msg); }
    }
    private static class LogicoException extends Exception {
        LogicoException(String msg) { super(msg); }
    }
}
