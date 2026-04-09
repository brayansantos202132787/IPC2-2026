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
public class Cancelacion {
     private int        idCancelacion;
    private int        idReservacion;
    private String     numeroReservacion;      
    private LocalDate  fechaCancelacion;
    private BigDecimal montoReembolsado;
    private BigDecimal porcentajeReembolso;
    private BigDecimal perdidaAgencia;
 
    public Cancelacion() {}
 
    
    public int        getIdCancelacion()     { return idCancelacion;     }
    public int        getIdReservacion()     { return idReservacion;     }
    public String     getNumeroReservacion() { return numeroReservacion;  }
    public LocalDate  getFechaCancelacion()  { return fechaCancelacion;  }
    public BigDecimal getMontoReembolsado()  { return montoReembolsado;  }
    public BigDecimal getPorcentajeReembolso(){ return porcentajeReembolso;}
    public BigDecimal getPerdidaAgencia()    { return perdidaAgencia;    }
 

    public void setIdCancelacion      (int        idCancelacion)      { this.idCancelacion      = idCancelacion;      }
    public void setIdReservacion      (int        idReservacion)      { this.idReservacion      = idReservacion;      }
    public void setNumeroReservacion  (String     numeroReservacion)  { this.numeroReservacion  = numeroReservacion;  }
    public void setFechaCancelacion   (LocalDate  fechaCancelacion)   { this.fechaCancelacion   = fechaCancelacion;   }
    public void setMontoReembolsado   (BigDecimal montoReembolsado)   { this.montoReembolsado   = montoReembolsado;   }
    public void setPorcentajeReembolso(BigDecimal porcentajeReembolso){ this.porcentajeReembolso = porcentajeReembolso;}
    public void setPerdidaAgencia     (BigDecimal perdidaAgencia)     { this.perdidaAgencia     = perdidaAgencia;     }
}
