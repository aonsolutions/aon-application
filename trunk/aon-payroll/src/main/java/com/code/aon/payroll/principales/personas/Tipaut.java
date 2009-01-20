package com.code.aon.payroll.principales.personas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Mantenimiento de Tipaut
 */
@Entity
@Table(name = "tipaut")
public class Tipaut implements ITransferObject {

	private String cdg;
	private String descripcion;

	// private List<Trabajo> trabajoList = new ArrayList<Trabajo>(0);

	@Id
	// @DataDefinition(label="Codigo de Tipo de Autorización")
	@Column(name = "cdg", unique = true, nullable = false, length = 3)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	// @DataDefinition(label="Descripcion de Tipo de
	// Autorización",descriptionColumn=true)
	@Column(name = "descripcion", nullable = false, length = 70)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/*
	 * @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,
	 * mappedBy="tipaut") public List<Trabajo> getTrabajoList() { return
	 * this.trabajoList; }
	 * 
	 * public void setTrabajoList(List<Trabajo> trabajoList) { this.trabajoList =
	 * trabajoList; }
	 */

}
