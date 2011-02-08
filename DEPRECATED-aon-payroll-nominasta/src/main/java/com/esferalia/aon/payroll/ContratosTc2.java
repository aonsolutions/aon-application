package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IContratosTc2;

@Entity
@Table(name = "tipcotc2")
public class ContratosTc2 implements ITransferObject, IContratosTc2 {

	private static final long serialVersionUID = 379975836228584001L;
	
	private String cdg;
	private String descripcion;
	private String descripcionAbreviada;
	private String cdgAntiguo;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 3)
	@Override
	public String getCdg() {
		return this.cdg;
	}
	@Override
	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name = "descripcion", length = 150)
	@Override
	public String getDescripcion() {
		return this.descripcion;
	}
	@Override
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "desabr", length = 30)
	@Override
	public String getDescripcionAbreviada() {
		return this.descripcionAbreviada;
	}
	@Override
	public void setDescripcionAbreviada(String descripcionAbreviada) {
		this.descripcionAbreviada = descripcionAbreviada;
	}

	@Column(name = "cdgant", length = 125)
	@Override
	public String getCdgAntiguo() {
		return this.cdgAntiguo;
	}
	@Override
	public void setCdgAntiguo(String cdgAntiguo) {
		this.cdgAntiguo = cdgAntiguo;
	}

}
