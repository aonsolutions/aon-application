package com.code.aon.payroll.principales.personas;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.tipos.Incidencia;

/**
 * Mantenimiento de Trabinci
 */
@Entity
@Table(name = "trabinci")
public class Trabinci implements ITransferObject {

	private TrabinciPK id;
	private Date fecfin;
	private Integer cantidad;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private BigDecimal importe;
	private Incidencia tipinc;
	private Trabajador emprper;
	private Empresa empresa;
	private Persona persona;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)),
			@AttributeOverride(name = "codinc", column = @Column(name = "codinc", nullable = false, length = 10)) })
	public TrabinciPK getId() {
		return this.id;
	}

	public void setId(TrabinciPK id) {
		this.id = id;
	}

	/**
	 * Fecha Fin Incidencia
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin", nullable = false)
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	// @DataDefinition(label="Horas, Dias")
	@Column(name = "cantidad", nullable = false, length = 2)
	public Integer getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Creacion Fila")
	@Column(name = "fecnew")
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Creacion Fila")
	@Column(name = "hornew")
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Modificacion Fila")
	@Column(name = "fecmod")
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Modificacion Fila")
	@Column(name = "hormod")
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	// @DataDefinition(label="Importe")
	@Column(name = "importe", scale=2, precision=8)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codinc", insertable = false, updatable = false)
	public Incidencia getTipinc() {
		return this.tipinc;
	}

	public void setTipinc(Incidencia tipinc) {
		this.tipinc = tipinc;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
		//this.empresa = emprper.getEmpresa();
		//this.persona = emprper.getPersona();
	}

	/*
	@Transient
	public Empresa getEmpresa() {
		Empresa empresa;
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
		this.emprper.setEmpresa(empresa);
	}
	*/

	/*
	@Transient
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
		this.emprper.setPersona(persona);
	}
	*/

}
