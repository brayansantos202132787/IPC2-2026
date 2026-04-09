/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

/**
 *
 * @author braya
 */
public class Rol {
     private int    idRol;
    private String nombre;
 
    public Rol() {}
 
    public Rol(int idRol, String nombre) {
        this.idRol  = idRol;
        this.nombre = nombre;
    }
 
    public int    getIdRol()  { return idRol;  }
    public String getNombre() { return nombre; }
 
    public void setIdRol (int    idRol)  { this.idRol  = idRol;  }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

