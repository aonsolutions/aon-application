package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

/**
 * Trabajador
 */
@Entity
@Table(name = "emprper")
public class Empleado implements ITransferObject,IEmpleado {

	private static final long serialVersionUID = 5020902333687926636L;
	
	private Integer id;
	private String codccc;
	private Date fechaInicio;
	private Date fechaFin;
	private Boolean mayor65;
	private IEmpresa empresa;
	private IActividad actividad;
	private IPersona persona;
	
	
	public Empleado() {
	}
	
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "codccc", length = 1)
	public String getCodccc() {
		return this.codccc;
	}

	public void setCodccc(String codccc) {
		this.codccc = codccc;
	}


	@Temporal(TemporalType.DATE)
	@Column(name = "fecalt", nullable = false)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecbaj")
	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Type(type="siNoType" )
	@Column(name = "mayor65", length = 1)
	@Override
	public boolean isMayor65() {
		return this.mayor65;
	}
	@Override
	public void setMayor65(boolean mayor65) {
		this.mayor65 = mayor65;
	}

	@ManyToOne(targetEntity = Empresa.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", referencedColumnName = "cdg", nullable = false)
	public IEmpresa getEmpresa() {
		return this.empresa;
	}
	public void setEmpresa(IEmpresa empresa) {
		this.empresa = empresa;
	}

	@ManyToOne(targetEntity = Actividad.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codact", nullable = false)
	public IActividad getActividad() {
		return this.actividad;
	}
	public void setActividad(IActividad actividad) {
		this.actividad = actividad;
	}

	@ManyToOne(targetEntity = Persona.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "codper", nullable = false)
	public IPersona getPersona() {
		return this.persona;
	}

	public void setPersona(IPersona persona) {
		this.persona = persona;
	}

	@Override
	@Transient
	public CuentaCotizacion getCuentaCotizacion() {
		if ("P".equals( getCodccc())) {
			return CuentaCotizacion.PRINCIPAL;
		} else if ("A".equals( getCodccc())) {
			return CuentaCotizacion.ALTO_CARGO;
		} else if ("R".equals( getCodccc())) {
			return CuentaCotizacion.APRENDIZ;
		} else if ("S".equals( getCodccc())) {
			return CuentaCotizacion.ASIMILADO;
		}
		return null;
	}
	@Override
	public void setCuentaCotizacion(CuentaCotizacion cc) {
		if (cc == CuentaCotizacion.PRINCIPAL) {
			setCodccc("P");
		} else if (cc == CuentaCotizacion.ALTO_CARGO) {
			setCodccc("A");
		} else if (cc == CuentaCotizacion.APRENDIZ) {
			setCodccc("R");
		} else if (cc == CuentaCotizacion.ASIMILADO) {
			setCodccc("S");
		} else {
			setCodccc(null);	
		}
		
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
