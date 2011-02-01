package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

@Entity
@Table(name = "emprccc")
public class ActividadCCC implements ITransferObject, IActividadCCC {

	private static final long serialVersionUID = -8611397701770462809L;

	private ActividadCCCPK id;
	private String descripcion;
	private IActividad actividad;
	
	@EmbeddedId
	public ActividadCCCPK getId() {
		return this.id;
	}

	public void setId(ActividadCCCPK id) {
		this.id = id;
	}

	@Column(name = "descripcion", length = 11)
	public String getDescripcion() {
		return this.descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@ManyToOne(targetEntity = Actividad.class,fetch = FetchType.EAGER)
	@Override
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public IActividad getActividad() {
		return this.actividad;
	}
	@Override
	public void setActividad(IActividad actividad) {
		this.actividad = actividad;
	}

	@Override
	@Transient
	public CuentaCotizacion getCuentaCotizacion() {
		if (getId() == null) {
			return null;
		} else if ("P".equals( getId().getTipccc())) {
			return CuentaCotizacion.PRINCIPAL;
		} else if ("A".equals( getId().getTipccc())) {
			return CuentaCotizacion.ALTO_CARGO;
		} else if ("R".equals( getId().getTipccc())) {
			return CuentaCotizacion.APRENDIZ;
		} else if ("S".equals( getId().getTipccc())) {
			return CuentaCotizacion.ASIMILADO;
		}
		return null;
	}
	@Override
	public void setCuentaCotizacion(CuentaCotizacion cc) {
		if (getId() == null) {
			setId( new ActividadCCCPK());
		}
		if (cc == CuentaCotizacion.PRINCIPAL) {
			getId().setTipccc("P");
		} else if (cc == CuentaCotizacion.ALTO_CARGO) {
			getId().setTipccc("A");
		} else if (cc == CuentaCotizacion.APRENDIZ) {
			getId().setTipccc("R");
		} else if (cc == CuentaCotizacion.ASIMILADO) {
			getId().setTipccc("S");
		} else {
			getId().setTipccc(null);	
		}
	}


}
