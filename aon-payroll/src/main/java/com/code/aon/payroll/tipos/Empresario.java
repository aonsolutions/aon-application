package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Tipos de Empresarios.
 * 
 * @author eagirrezabal
 *
 */
@Entity
@Table(name="tipempr")
public class Empresario  implements ITransferObject {

     private String cdg;
     private String description;

     /**
      * Devuelve el codigo del tipo de empresario.
      * 
      * @return
      */
    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=1)
    public String getCdg() {
        return this.cdg;
    }
    
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Devuelve la descripción del tipo de empresario.
     * 
     * @return
     */
    @Column(name="descripcion", nullable=false, length=25)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}