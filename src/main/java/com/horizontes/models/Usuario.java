/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

/**
 *
 * @author braya
 */
public class Usuario {
    private int     idUsuario;
    private String  nombre;
    private String  password;   
    private int     idRol;
    private String  nombreRol;  
    private boolean activo;
 
    public Usuario() {}
 
    
    public int     getIdUsuario() { return idUsuario; }
    public String  getNombre()    { return nombre;    }
    public String  getPassword()  { return password;  }
    public int     getIdRol()     { return idRol;     }
    public String  getNombreRol() { return nombreRol; }
    public boolean isActivo()     { return activo;    }
 
    
    public void setIdUsuario(int     idUsuario) { this.idUsuario = idUsuario; }
    public void setNombre   (String  nombre)    { this.nombre    = nombre;    }
    public void setPassword (String  password)  { this.password  = password;  }
    public void setIdRol    (int     idRol)     { this.idRol     = idRol;     }
    public void setNombreRol(String  nombreRol) { this.nombreRol = nombreRol; }
    public void setActivo   (boolean activo)    { this.activo    = activo;    }
}
