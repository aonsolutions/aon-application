package com.code.aon.payroll.auxiliares;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Sistema RED: Colectivos
 * 
 * @author eagirrezabal
 *
 */

@Entity
@Table(name="colectivos")
public class Colectivos  implements ITransferObject {

  private String cdg;
  private String description;
  private String descripcorta;


/**
 * Devuelve el código de colectivos
 * 
 * @return
 */
 @Id     
 @Column(name="cdg", unique=true, nullable=false, length=4)
 public String getCdg() {
     return this.cdg;
 }
 
 public void setCdg(String cdg) {
     this.cdg = cdg;
 }
 
 /**
  * Devuelve la descripción de colectivos
  * 
  * @return
  */
 @Column(name="descripcion", length=60)
 public String getDescription() {
     return this.description;
 }
 
 public void setDescription(String description) {
     this.description = description;
 }
 
 /**
  * Devuelve la descripción corta de colectivos
  * @return
  */	
 @Column(name="descripcorta", length=30)
 public String getDescripcorta() {
     return this.descripcorta;
 }
 
 public void setDescripcorta(String descripcorta) {
     this.descripcorta = descripcorta;
 }


}


