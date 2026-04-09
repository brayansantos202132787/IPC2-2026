/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;


import java.time.LocalDate;

/**
 *
 * @author braya
 */
public class Cliente {
     private int       idCliente;
    private String    dpi;
    private String    nombre;
    private LocalDate fechaNac;
    private String    telefono;
    private String    email;
    private String    nacionalidad;
 
    public Cliente() {}
 
    
    public int       getIdCliente()   { return idCliente;   }
    public String    getDpi()         { return dpi;         }
    public String    getNombre()      { return nombre;      }
    public LocalDate getFechaNac()    { return fechaNac;    }
    public String    getTelefono()    { return telefono;    }
    public String    getEmail()       { return email;       }
    public String    getNacionalidad(){ return nacionalidad;}
 
  
    public void setIdCliente   (int       idCliente)    { this.idCliente    = idCliente;    }
    public void setDpi         (String    dpi)          { this.dpi          = dpi;          }
    public void setNombre      (String    nombre)       { this.nombre       = nombre;       }
    public void setFechaNac    (LocalDate fechaNac)     { this.fechaNac     = fechaNac;     }
    public void setTelefono    (String    telefono)     { this.telefono     = telefono;     }
    public void setEmail       (String    email)        { this.email        = email;        }
    public void setNacionalidad(String    nacionalidad) { this.nacionalidad = nacionalidad; }
}
