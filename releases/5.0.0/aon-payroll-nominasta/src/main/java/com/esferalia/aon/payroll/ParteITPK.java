package com.esferalia.aon.payroll;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class ParteITPK implements Serializable {

	private static final long serialVersionUID = -775104683033314551L;
	
	private int id;
	private Date fechaBaja;

	@Column(name = "cdg", nullable = false, length = 4)
	public int getId() {
		return this.id;
	}
	public void setCdg(int id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja; 
	}
	
}
