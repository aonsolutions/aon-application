 package com.code.aon.payroll.cotizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Agrupador, Maestro de Porcentajes de Cotización.
 */
@Entity
@Table(name="porcoti")
public class PorcentajeMaestro implements ITransferObject {

	private String cdg;
	private String description;
	private Integer ordpct;

	@Id
	@Column(name="cdg", unique=true, nullable=false, length=8)
    public String getCdg() {
		return this.cdg;
    }

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}
    
	@Column(name="descripcion", nullable=false, length=50)
	public String getDescription() {
		return this.description;
	}
    
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	@Column(name="ordpct", length=2)
	public Integer getOrdpct() {
		return this.ordpct;
	}

	public void setOrdpct(Integer ordpct) {
		this.ordpct = ordpct;
	}

}
