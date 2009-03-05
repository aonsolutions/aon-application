package com.code.aon.payroll.irpf;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  Clave primaria de Cuotas de Retención.
 * 
 * @author eagirrezabal
 *
 */

@Embeddable
public class CuotaPK implements Serializable{
	
	private Integer numTramo;
	private Date fecini;
	
	@Column(name="num_tramo", nullable=false, length=10)
	public Integer getNumTramo() {
		return numTramo;
	}
	public void setNumTramo(Integer numTramo) {
		this.numTramo = numTramo;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecini", nullable=false)
	public Date getFecini() {
		return fecini;
	}
	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}
	
	
}	
