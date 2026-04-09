/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

import java.math.BigDecimal;

/**
 *
 * @author braya
 */
public class ServicioPaquete {
     private int        idServicio;
    private int        idPaquete;
    private int        idProveedor;
    private String     nombreProveedor;   
    private String     descripcion;
    private BigDecimal costoProveedor;
 
    public ServicioPaquete() {}
 
    
    public int        getIdServicio()      { return idServicio;      }
    public int        getIdPaquete()       { return idPaquete;       }
    public int        getIdProveedor()     { return idProveedor;     }
    public String     getNombreProveedor() { return nombreProveedor; }
    public String     getDescripcion()     { return descripcion;     }
    public BigDecimal getCostoProveedor()  { return costoProveedor;  }
 

    public void setIdServicio     (int        idServicio)      { this.idServicio      = idServicio;      }
    public void setIdPaquete      (int        idPaquete)       { this.idPaquete       = idPaquete;       }
    public void setIdProveedor    (int        idProveedor)     { this.idProveedor     = idProveedor;     }
    public void setNombreProveedor(String     nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    public void setDescripcion    (String     descripcion)     { this.descripcion     = descripcion;     }
    public void setCostoProveedor (BigDecimal costoProveedor)  { this.costoProveedor  = costoProveedor;  }
}
