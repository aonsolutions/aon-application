package com.code.aon.payroll.principales.personas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Mantenimiento de Tipinc
 */
@Entity
@Table(name = "tipinc")
public class Tipinc implements ITransferObject {

	private String cdg;
	private String descripcion;
	private String indresta;
	private String inddto;

	// private List<Trabinci> trabinciList = new ArrayList<Trabinci>(0);

	@Id
	// @DataDefinition(label="Codigo de Incidencia")
	@Column(name = "cdg", unique = true, nullable = false, length = 10)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	// @DataDefinition(label="Descripcion de Incidencia",descriptionColumn=true)
	@Column(name = "descripcion", nullable = false, length = 25)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	// @DataDefinition(label="Indica si resta dias en el calculo de la nomina")
	@Column(name = "indresta", length = 1)
	public String getIndresta() {
		return this.indresta;
	}

	public void setIndresta(String indresta) {
		this.indresta = indresta;
	}

	// @DataDefinition(label="Indica si Descuenta Dias en Paga Extra")
	@Column(name = "inddto", length = 1)
	public String getInddto() {
		return this.inddto;
	}

	public void setInddto(String inddto) {
		this.inddto = inddto;
	}

	/*
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tipinc")
	public List<Trabinci> getTrabinciList() {
		return this.trabinciList;
	}

	public void setTrabinciList(List<Trabinci> trabinciList) {
		this.trabinciList = trabinciList;
	}
	*/

}
