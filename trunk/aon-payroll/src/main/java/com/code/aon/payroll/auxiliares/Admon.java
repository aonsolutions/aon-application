package com.code.aon.payroll.auxiliares;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Organismos y entidades: Mantenimiento de Administraciones de Hacienda
 * 
 * @author eagirrezabal
 *
 */

@Entity
@Table(name="admon")
public class Admon implements ITransferObject {

  private String cdg;
  private String description;

/**
 * Devuleve el cógido de Administraciones
 * 
 * @return
 */
 @Id     
 @Column(name="cdg", unique=true, nullable=false, length=5)
 public String getCdg() {
     return this.cdg;
 }
 
 public void setCdg(String cdg) {
     this.cdg = cdg;
 }
 
 /**
  * Devuelve la descripción de Administraciones
  * 
  * @return
  */
 @Column(name="descripcion", nullable=false, length=35)
 public String getDescription() {
     return this.description;
 }
 
 public void setDescription(String description) {
     this.description = description;
 }


}


