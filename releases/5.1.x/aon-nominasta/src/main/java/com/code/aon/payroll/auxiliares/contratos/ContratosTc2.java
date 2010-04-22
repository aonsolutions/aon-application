package com.code.aon.payroll.auxiliares.contratos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Contratos Tc2.
 */
@Entity
@Table(name = "tipcotc2")
public class ContratosTc2 implements ITransferObject {

	private String cdg;
	private String description;
	private String desabr;
	private String cdgant;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 3)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name = "descripcion", length = 150)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "desabr", length = 30)
	public String getDesabr() {
		return this.desabr;
	}

	public void setDesabr(String desabr) {
		this.desabr = desabr;
	}

	@Column(name = "cdgant", length = 125)
	public String getCdgant() {
		return this.cdgant;
	}

	public void setCdgant(String cdgant) {
		this.cdgant = cdgant;
	}

}
