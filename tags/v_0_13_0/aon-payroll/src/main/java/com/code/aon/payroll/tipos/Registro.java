package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;


/**
 * Tipos de Registros.
 * 
 * @author alatorre
 *
 */
@Entity
@Table(name="tipreg")
public class Registro implements ITransferObject{
	
	//private static final long serialVersionUID = 6239565575500663399L;

	private String cdg;
	private String description;
	
	/**
	 * Devuelve el codigo del tipo de registro.
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
	 * Devuelve la descripcion del tipo de registro.
	 * 
	 * @return
	 */
	@Column(name="descripcion", nullable=false, length=60)
	public String getDescription() {
		return this.description;
	}
    
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}
	
}	
