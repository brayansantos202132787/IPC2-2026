/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author braya
 */
public class Paquete {
     private int                 idPaquete;
    private String              nombre;
    private int                 idDestino;
    private String              nombreDestino;   // campo enriquecido
    private int                 duracionDias;
    private String              descripcion;
    private BigDecimal          precioVenta;
    private int                 capacidad;
    private boolean             activo;
    private List<ServicioPaquete> servicios;     
 
    public Paquete() {}
 
  
    public int                 getIdPaquete()     { return idPaquete;     }
    public String              getNombre()        { return nombre;        }
    public int                 getIdDestino()     { return idDestino;     }
    public String              getNombreDestino() { return nombreDestino; }
    public int                 getDuracionDias()  { return duracionDias;  }
    public String              getDescripcion()   { return descripcion;   }
    public BigDecimal          getPrecioVenta()   { return precioVenta;   }
    public int                 getCapacidad()     { return capacidad;     }
    public boolean             isActivo()         { return activo;        }
    public List<ServicioPaquete> getServicios()   { return servicios;     }
 
    
    public void setIdPaquete    (int                  idPaquete)     { this.idPaquete     = idPaquete;     }
    public void setNombre       (String               nombre)        { this.nombre        = nombre;        }
    public void setIdDestino    (int                  idDestino)     { this.idDestino     = idDestino;     }
    public void setNombreDestino(String               nombreDestino) { this.nombreDestino = nombreDestino; }
    public void setDuracionDias (int                  duracionDias)  { this.duracionDias  = duracionDias;  }
    public void setDescripcion  (String               descripcion)   { this.descripcion   = descripcion;   }
    public void setPrecioVenta  (BigDecimal            precioVenta)   { this.precioVenta   = precioVenta;   }
    public void setCapacidad    (int                  capacidad)     { this.capacidad     = capacidad;     }
    public void setActivo       (boolean              activo)        { this.activo        = activo;        }
    public void setServicios    (List<ServicioPaquete> servicios)    { this.servicios     = servicios;     }
}
