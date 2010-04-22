package com.code.aon.payroll.principales.personas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;

/**
 * Mantenimiento de Tipocont
 */
@Entity
@Table(name = "tipocont")
public class Tipocont implements ITransferObject {

	private String cdg;
	private String descripcion;
	private String desemple;
	private String mujersub;
	private String incaread;
	private String primertra;
	private Integer gradomin;
	private String excsocial;
	private PorcentajeMaestro porcoti;

	// private List<Httrabajador> httrabajadorList = new
	// ArrayList<Httrabajador>(0);
	// private List<Trabajo> trabajoList = new ArrayList<Trabajo>(0);

	@Id
	// @DataDefinition(label="Codigo de Contrato Interno")
	@Column(name = "cdg", unique = true, nullable = false, length = 2)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	// @DataDefinition(label="Descripcion de Contrato
	// Interno",descriptionColumn=true)
	@Column(name = "descripcion", length = 60)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	// @DataDefinition(label="Desempleado")
	@Column(name = "desemple", length = 1)
	public String getDesemple() {
		return this.desemple;
	}

	public void setDesemple(String desemple) {
		this.desemple = desemple;
	}

	// @DataDefinition(label="Mujer subrepresentada")
	@Column(name = "mujersub", length = 1)
	public String getMujersub() {
		return this.mujersub;
	}

	public void setMujersub(String mujersub) {
		this.mujersub = mujersub;
	}

	// @DataDefinition(label="Incapacitado readmitido")
	@Column(name = "incaread", length = 1)
	public String getIncaread() {
		return this.incaread;
	}

	public void setIncaread(String incaread) {
		this.incaread = incaread;
	}

	// @DataDefinition(label="Primer trabajador contratado por autonomo")
	@Column(name = "primertra", length = 1)
	public String getPrimertra() {
		return this.primertra;
	}

	public void setPrimertra(String primertra) {
		this.primertra = primertra;
	}

	// @DataDefinition(label="Grado de minusvalia")
	@Column(name = "gradomin", length = 2)
	public Integer getGradomin() {
		return this.gradomin;
	}

	public void setGradomin(Integer gradomin) {
		this.gradomin = gradomin;
	}

	// @DataDefinition(label="Exclusion social")
	@Column(name = "excsocial", length = 1)
	public String getExcsocial() {
		return this.excsocial;
	}

	public void setExcsocial(String excsocial) {
		this.excsocial = excsocial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codpct")
	public PorcentajeMaestro getPorcoti() {
		return this.porcoti;
	}

	public void setPorcoti(PorcentajeMaestro porcoti) {
		this.porcoti = porcoti;
	}

	/*
	 * @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,
	 * mappedBy="tipocont") public List<Httrabajador> getHttrabajadorList() {
	 * return this.httrabajadorList; }
	 * 
	 * public void setHttrabajadorList(List<Httrabajador> httrabajadorList) {
	 * this.httrabajadorList = httrabajadorList; }
	 * 
	 * @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY,
	 * mappedBy="tipocont") public List<Trabajo> getTrabajoList() { return
	 * this.trabajoList; }
	 * 
	 * public void setTrabajoList(List<Trabajo> trabajoList) { this.trabajoList =
	 * trabajoList; }
	 */

}
