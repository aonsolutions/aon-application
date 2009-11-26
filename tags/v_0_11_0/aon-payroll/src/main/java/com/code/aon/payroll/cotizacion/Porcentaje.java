package com.code.aon.payroll.cotizacion;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Porcentaje de Cotización.
 */
@Entity
@Table(name="linporco")
public class Porcentaje implements ITransferObject {

	private PorcentajePK id;
	private Date fecfin;
	private BigDecimal pctemp;
	private BigDecimal pcttra;
	private BigDecimal pcttot;
	private PorcentajeMaestro maestro;

	@EmbeddedId    
	@AttributeOverrides( {
		@AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=8) ), 
		@AttributeOverride(name="fecini", column=@Column(name="fecini", nullable=false, length=10) ) } )
	public PorcentajePK getId() {
		return this.id;
	}

	public void setId(PorcentajePK id) {
		this.id = id;
	}

	@Column(name="fecfin", nullable=false, length=10)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	@Column(name="pctemp", precision=3, scale=2)
	public BigDecimal getPctemp() {
		return this.pctemp;
	}

	public void setPctemp(BigDecimal pctemp) {
		this.pctemp = pctemp;
	}

	@Column(name="pcttra", precision=3, scale=2)
	public BigDecimal getPcttra() {
		return this.pcttra;
	}

	public void setPcttra(BigDecimal pcttra) {
		this.pcttra = pcttra;
	}

	@Column(name="pcttot", precision=3, scale=2)
	public BigDecimal getPcttot() {
		return this.pcttot;
	}

	public void setPcttot(BigDecimal pcttot) {
		this.pcttot = pcttot;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cdg", insertable=false, updatable=false)
	public PorcentajeMaestro getPorcentajeMaestro() {
		return this.maestro;
	}

	public void setPorcentajeMaestro(PorcentajeMaestro maestro) {
		this.maestro = maestro;
	}

}


