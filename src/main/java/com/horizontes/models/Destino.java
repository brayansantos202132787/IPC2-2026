/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

/**
 *
 * @author braya
 */
public class Destino {
    private int    idDestino;
    private String nombre;
    private String pais;
    private String descripcion;
    private String clima;
    private String mejorEpoca;
    private String imagenUrl;
 
    public Destino() {}
 
    
    public int    getIdDestino()  { return idDestino;  }
    public String getNombre()     { return nombre;     }
    public String getPais()       { return pais;       }
    public String getDescripcion(){ return descripcion;}
    public String getClima()      { return clima;      }
    public String getMejorEpoca() { return mejorEpoca; }
    public String getImagenUrl()  { return imagenUrl;  }
 
   
    public void setIdDestino  (int    idDestino)   { this.idDestino   = idDestino;   }
    public void setNombre     (String nombre)      { this.nombre      = nombre;      }
    public void setPais       (String pais)        { this.pais        = pais;        }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setClima      (String clima)       { this.clima       = clima;       }
    public void setMejorEpoca (String mejorEpoca)  { this.mejorEpoca  = mejorEpoca;  }
    public void setImagenUrl  (String imagenUrl)   { this.imagenUrl   = imagenUrl;   }
}
