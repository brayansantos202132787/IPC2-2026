/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

/**
 *
 * @author braya
 */
public class Proveedor {
        private int    idProveedor;
    private String nombre;
    private int    idTipo;
    private String nombreTipo;  
    private String pais;
    private String contacto;
 
    public Proveedor() {}
 
    
    public int    getIdProveedor() { return idProveedor; }
    public String getNombre()      { return nombre;      }
    public int    getIdTipo()      { return idTipo;      }
    public String getNombreTipo()  { return nombreTipo;  }
    public String getPais()        { return pais;        }
    public String getContacto()    { return contacto;    }
 
    
    public void setIdProveedor(int    idProveedor) { this.idProveedor = idProveedor; }
    public void setNombre     (String nombre)      { this.nombre      = nombre;      }
    public void setIdTipo     (int    idTipo)      { this.idTipo      = idTipo;      }
    public void setNombreTipo (String nombreTipo)  { this.nombreTipo  = nombreTipo;  }
    public void setPais       (String pais)        { this.pais        = pais;        }
    public void setContacto   (String contacto)    { this.contacto    = contacto;    }

}
