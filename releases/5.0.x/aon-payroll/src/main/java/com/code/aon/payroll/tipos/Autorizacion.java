package com.code.aon.payroll.tipos;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Tipos de Autorizacion Extranjeros
 * 
 * @author alatorre
 *
 */
@Entity
@Table(name="tipaut")
public class Autorizacion  implements ITransferObject {

 
	private static final long serialVersionUID = -1449383349556643803L;
	
private String cdg;
private String description;
 

/**
 * Devuelve el codigo del tipo de autorizacion.
 * 
 * @return
 */
 @Id   	
 @Column(name="cdg", unique=true, nullable=false, length=3)
 public String getCdg() {
     return this.cdg;
 }
 
 public void setCdg(String cdg) {
     this.cdg = cdg;
 }
 
     /**
	 * Devuelve la descripcion del tipo de autorizacion.
	 * 
	 * @return
	 */	
 @Column(name="descripcion", nullable=false, length=70)
 public String getDescription() {
     return this.description;
 }
 
 public void setDescription(String descripcion) {
     this.description = descripcion;
 }


}
