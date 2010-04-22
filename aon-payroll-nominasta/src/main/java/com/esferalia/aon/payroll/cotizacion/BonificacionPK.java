package com.esferalia.aon.payroll.cotizacion;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;

@Embeddable
public class BonificacionPK implements Serializable, Comparable<BonificacionPK> {

	private static final long serialVersionUID = -4883957832446463857L;
	
	private Integer numero;
	private Integer cdg;
	private Date fechaInicio;
	
	@Column(name = "numero", nullable = false, length = 4)
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "cdg", nullable = false, length = 2)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BonificacionPK))
			return false;
		BonificacionPK castOther = (BonificacionPK) other;
		return (this.getNumero() == castOther.getNumero())
				&& (this.getCdg() == castOther.getCdg())
				&& ((this.getFechaInicio() == castOther.getFechaInicio()) || (this
						.getFechaInicio() != null
						&& castOther.getFechaInicio() != null && this.getFechaInicio()
						.equals(castOther.getFechaInicio())));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + this.getNumero();
		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFechaInicio() == null ? 0 : this.getFechaInicio().hashCode());
		return result;
	}
	
	@Override
	public int compareTo(BonificacionPK o) {
		BonificacionPK myClass = (BonificacionPK) o;
	     return new CompareToBuilder()
	       .append(this.cdg, myClass.cdg)
	       .append(this.numero, myClass.numero)
	       .append(this.fechaInicio, myClass.fechaInicio)
	       .toComparison();
   }
	
}
