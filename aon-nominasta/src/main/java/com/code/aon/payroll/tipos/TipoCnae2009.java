package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Tipos de CNAE2009
 * 
 * @author alatorre
 *
 */


@Entity
@Table(name="tipocnae2009")
public class TipoCnae2009 implements ITransferObject {

  private String cdg;
  private String description;
  private String seccion;

 /**
  * Devuelve el código de CNAE 2009
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
  * Devuelve la descripción de CNAE 2009
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
 
 /**
  * Devuelve la sección de CNAE 2009
  * 
  * @return
  */
 @Column(name="seccion", length=1)
 public String getSeccion() {
     return this.seccion;
 }
 
 public void setSeccion(String seccion) {
     this.seccion = seccion;
 }

}
