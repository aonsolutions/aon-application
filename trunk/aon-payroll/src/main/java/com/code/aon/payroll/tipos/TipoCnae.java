package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Tipos de CNAE.
 * 
 * @author eagirrezabal
 *
 */


@Entity
@Table(name="tipocnae")
public class TipoCnae implements ITransferObject {

  private String cdg;
  private String description;

 /**
  * Devuelve el código de CNAE
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
  * Devuelve la descripción de CNAE
  * 
  * @return
  */
 @Column(name="descripcion", length=250)
 public String getDescription() {
     return this.description;
 }
 
 public void setDescription(String description) {
     this.description = description;
 }

}


