package com.code.aon.payroll.cotizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Agrupador, Maestro de Cnae 2009
 * 
 * author: alatorre
 */
@Entity
@Table(name="cnae2009")
public class Cnae2009Maestro  implements ITransferObject {

     private String cdg;
     private String description;
     private String ocupacion;
   
    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=5)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }
    
    @Column(name="descripcion", length=250)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name="ocupacion", length=25)
    public String getOcupacion() {
        return this.ocupacion;
    }
    
    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

}