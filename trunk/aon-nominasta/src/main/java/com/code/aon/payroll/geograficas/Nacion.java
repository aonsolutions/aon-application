package com.code.aon.payroll.geograficas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Tipos de Documento.
 */
@Entity
@Table(name="nacion")
public class Nacion implements ITransferObject {



	private String cdg;
	private String description;

	@Id
	@Column(name="cdg", unique=true, nullable=false, length=3)
    public String getCdg() {
		return this.cdg;
    }

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}
    
	@Column(name="descripcion", nullable=false, length=60)
	public String getDescription() {
		return this.description;
	}
    
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

}