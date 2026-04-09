/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
/**
 *
 * @author braya
 */
public class Reservacion {
     private int        idReservacion;
    private String     numeroReservacion;
    private int        idPaquete;
    private String     nombrePaquete;       
    private int        idUsuario;
    private String     nombreUsuario;       
    private LocalDate  fechaCreacion;
    private LocalDate  fechaViaje;
    private int        cantidadPasajeros;
    private BigDecimal costoTotal;
    private int        idEstado;
    private String     nombreEstado;        
    private List<Cliente> pasajeros;        
 
    public Reservacion() {}
 
    
    public int        getIdReservacion()    { return idReservacion;    }
    public String     getNumeroReservacion(){ return numeroReservacion; }
    public int        getIdPaquete()        { return idPaquete;        }
    public String     getNombrePaquete()    { return nombrePaquete;    }
    public int        getIdUsuario()        { return idUsuario;        }
    public String     getNombreUsuario()    { return nombreUsuario;    }
    public LocalDate  getFechaCreacion()    { return fechaCreacion;    }
    public LocalDate  getFechaViaje()       { return fechaViaje;       }
    public int        getCantidadPasajeros(){ return cantidadPasajeros;}
    public BigDecimal getCostoTotal()       { return costoTotal;       }
    public int        getIdEstado()         { return idEstado;         }
    public String     getNombreEstado()     { return nombreEstado;     }
    public List<Cliente> getPasajeros()     { return pasajeros;        }
 
 
    public void setIdReservacion    (int          idReservacion)    { this.idReservacion    = idReservacion;    }
    public void setNumeroReservacion(String        numeroReservacion){ this.numeroReservacion = numeroReservacion;}
    public void setIdPaquete        (int          idPaquete)        { this.idPaquete        = idPaquete;        }
    public void setNombrePaquete    (String        nombrePaquete)    { this.nombrePaquete    = nombrePaquete;    }
    public void setIdUsuario        (int          idUsuario)        { this.idUsuario        = idUsuario;        }
    public void setNombreUsuario    (String        nombreUsuario)    { this.nombreUsuario    = nombreUsuario;    }
    public void setFechaCreacion    (LocalDate     fechaCreacion)    { this.fechaCreacion    = fechaCreacion;    }
    public void setFechaViaje       (LocalDate     fechaViaje)       { this.fechaViaje       = fechaViaje;       }
    public void setCantidadPasajeros(int          cantidadPasajeros){ this.cantidadPasajeros = cantidadPasajeros;}
    public void setCostoTotal       (BigDecimal    costoTotal)       { this.costoTotal       = costoTotal;       }
    public void setIdEstado         (int          idEstado)         { this.idEstado         = idEstado;         }
    public void setNombreEstado     (String        nombreEstado)     { this.nombreEstado     = nombreEstado;     }
    public void setPasajeros        (List<Cliente> pasajeros)       { this.pasajeros        = pasajeros;        }
}
