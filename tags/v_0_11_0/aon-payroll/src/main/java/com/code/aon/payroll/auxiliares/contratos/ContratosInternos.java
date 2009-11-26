package com.code.aon.payroll.auxiliares.contratos;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	private String desemple;
	private BigDecimal gradomin;
	private String mujersub;
	private String incaread;
	private String primertra;
	private String excsocial;

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

	@Column(name="desemple", length=1)
	public String getDesemple() {
		return this.desemple;
	}

	public void setDesemple(String desemple) {
		this.desemple = desemple;
		this.desempleenum = ( this.desemple != null)? Desempleado.valueOf( "D" + this.desemple ): null;
	}

	@Column(name="mujersub", length=1)
	public String getMujersub() {
		return this.mujersub;
	}

	public void setMujersub(String mujersub) {
		this.mujersub = mujersub;
	}

	@Column(name="incaread", length=1)
	public String getIncaread() {
		return this.incaread;
	}

	public void setIncaread(String incaread) {
		this.incaread = incaread;
	}

	@Column(name="primertra", length=1)
	public String getPrimertra() {
		return this.primertra;
	}

	public void setPrimertra(String primertra) {
		this.primertra = primertra;
	}

	@Column(name="excsocial", length=1)
	public String getExcsocial() {
		return this.excsocial;
	}

	public void setExcsocial(String excsocial) {
		this.excsocial = excsocial;
	}

	@Column(name="gradomin", length=2)
	public BigDecimal getGradomin() {
		return this.gradomin;
	}

	public void setGradomin(BigDecimal gradomin) {
		this.gradomin = gradomin;
	}

//TODO Problemas en la creacion del enumerado a partir de un String.
	private Desempleado desempleenum;
	@Transient 
	public Desempleado getDesempleenum() {
		return desempleenum;
	}
	public void setDesempleenum(Desempleado desempleenum) {
		this.desempleenum = desempleenum;
		setDesemple( (this.desempleenum != null)? this.desempleenum.name().substring( 1 ) : null );
	}
	
//TODO A la espera de implementar un SelectBooleanCheckboxRenderer.
	@Transient 
	public Boolean getMujersubbol() { 
		return (getMujersub() != null && getMujersub().equals("S")?true:false );
	}
	public void setMujersubbol(Boolean bol) {
		setMujersub( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getIncareadbol() {
		return (getIncaread() != null && getIncaread().equals("S")?true:false );
	}
	public void setIncareadbol(Boolean bol) {
		setIncaread( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getPrimertrabol() {
		return (getPrimertra() != null && getPrimertra().equals("S")?true:false );
	}
	public void setPrimertrabol(Boolean bol) {
		setPrimertra( (bol!=null && bol)? "S":"N" );
	}

	@Transient 
	public Boolean getExcsocialbol() {
		return (getExcsocial() != null && getExcsocial().equals("S")?true:false );
	}
	public void setExcsocialbol(Boolean bol) {
		setExcsocial( (bol!=null && bol)? "S":"N" );
	}
//	******************************************************************
}