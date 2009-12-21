package com.code.aon.payroll.irpfforal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Porcentajes de cotización.
 */
@Embeddable
public class MinorPK implements Serializable {

	

	private Integer numTramo;
	private Date fecini;

	@Column(name="num_tramo", nullable=false, length=8)
	public Integer getnumTramo() {
		return this.numTramo;
	}
   
	public void setnumTramo(Integer numTramo) {
		this.numTramo = numTramo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="fecini", nullable=false)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}



}
