/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author braya
 */
public class Pago {
     private int        idPago;
    private int        idReservacion;
    private String     numeroReservacion;   
    private BigDecimal monto;
    private int        idMetodo;
    private String     nombreMetodo;        
    private LocalDate  fechaPago;
 
    public Pago() {}
 
 
    public int        getIdPago()           { return idPago;           }
    public int        getIdReservacion()    { return idReservacion;    }
    public String     getNumeroReservacion(){ return numeroReservacion; }
    public BigDecimal getMonto()            { return monto;            }
    public int        getIdMetodo()         { return idMetodo;         }
    public String     getNombreMetodo()     { return nombreMetodo;     }
    public LocalDate  getFechaPago()        { return fechaPago;        }
 

    public void setIdPago           (int        idPago)           { this.idPago           = idPago;           }
    public void setIdReservacion    (int        idReservacion)    { this.idReservacion    = idReservacion;    }
    public void setNumeroReservacion(String     numeroReservacion){ this.numeroReservacion = numeroReservacion;}
    public void setMonto            (BigDecimal monto)            { this.monto            = monto;            }
    public void setIdMetodo         (int        idMetodo)         { this.idMetodo         = idMetodo;         }
    public void setNombreMetodo     (String     nombreMetodo)     { this.nombreMetodo     = nombreMetodo;     }
    public void setFechaPago        (LocalDate  fechaPago)        { this.fechaPago        = fechaPago;        }
}
