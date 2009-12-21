package com.code.aon.payroll.auxiliares.contratos;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.enumeration.Desempleado;

/**
 * Contratos Internos.
 */
@Entity
@Table(name="tipocont")
public class ContratosInternos implements ITransferObject {

	private String cdg;
	private String description;
	private PorcentajeMaestro maestro;
	private Desempleado desemple;
	private BigDecimal gradomin;
	private Boolean mujersub;
	private Boolean incaread;
	private Boolean primertra;
	private Boolean excsocial;

	@Id     
	@Column(name="cdg", unique=true, nullable=false, length=2)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name="descripcion", length=60)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="codpct", insertable=false, updatable=false)
	public PorcentajeMaestro getPorcentajeMaestro() {
		return this.maestro;
	}

	public void setPorcentajeMaestro(PorcentajeMaestro maestro) {
		this.maestro = maestro;
	}

	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Desempleado")} )
	@Column(name="desemple", length=1)
	public Desempleado getDesemple() {
		return this.desemple;
	}

	public void setDesemple(Desempleado desemple) {
		this.desemple = desemple;
	}

	@Type(type="siNoType" )
	@Column(name="mujersub", length=1)
	public Boolean getMujersub() {
		return mujersub;
	}

	public void setMujersub(Boolean mujersub) {
		this.mujersub = mujersub;
	}

	@Type(type="siNoType" )
	@Column(name="incaread", length=1)
	public Boolean getIncaread() {
		return incaread;
	}

	public void setIncaread(Boolean incaread) {
		this.incaread = incaread;
	}

	@Type(type="siNoType" )
	@Column(name="primertra", length=1)
	public Boolean getPrimertra() {
		return primertra;
	}

	public void setPrimertra(Boolean primertra) {
		this.primertra = primertra;
	}

	@Type(type="siNoType" )
	@Column(name="excsocial", length=1)
	public Boolean getExcsocial() {
		return excsocial;
	}

	public void setExcsocial(Boolean excsocial) {
		this.excsocial = excsocial;
	}

	@Column(name="gradomin", length=2)
	public BigDecimal getGradomin() {
		return this.gradomin;
	}

	public void setGradomin(BigDecimal gradomin) {
		this.gradomin = gradomin;
	}

}