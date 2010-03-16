package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Tipos de Documento.
 */
@Entity
@Table(name="tipdoc")
public class Documento implements ITransferObject {

	private static final long serialVersionUID = 4304438088705559359L;

	private String cdg;
	private String description;

	/**
	 * Devuelve el código del tipo de documento.
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
	 * Devuelve la descripción del tipo de documento.
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