package com.esferalia.aon.payroll;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class ParteITPK implements Serializable {

	private static final long serialVersionUID = -775104683033314551L;
	
	private int cdg;
	private Date fechaBaja;

	@Column(name = "cdg", nullable = false, length = 4)
	public int getCdg() {
		return this.cdg;
	}
	public void setCdg(int cdg) {
		this.cdg = cdg;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja; 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
