package com.code.aon.payroll.avanzadas.kartel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Linpercepcion
 */

@Embeddable
public class LinpercepcionPK implements Serializable {

	private String cdg;
	private Date fecinicio;

	/**
	 * Devuelve el Código Percepción 
	 * @return
	 */
	@Column(name = "cdg", nullable = false, length = 8)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	/**
	 * Devuelve la Fecha Inicio Vigencia
	 * @return
	 */
	@Column(name = "fecinicio", nullable = false, length = 10)
	public Date getFecinicio() {
		return this.fecinicio;
	}

	public void setFecinicio(Date fecinicio) {
		this.fecinicio = fecinicio;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof LinpercepcionPK))
			return false;
		LinpercepcionPK castOther = (LinpercepcionPK) other;

		return ((this.getCdg() == castOther.getCdg()) || (this.getCdg() != null
				&& castOther.getCdg() != null && this.getCdg().equals(
				castOther.getCdg())))
				&& ((this.getFecinicio() == castOther.getFecinicio()) || (this
						.getFecinicio() != null
						&& castOther.getFecinicio() != null && this
						.getFecinicio().equals(castOther.getFecinicio())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCdg() == null ? 0 : this.getCdg().hashCode());
		result = 37 * result
				+ (getFecinicio() == null ? 0 : this.getFecinicio().hashCode());
		return result;
	}
}
