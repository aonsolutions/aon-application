package com.code.aon.payroll.principales.persona;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Embargo
 */

@Embeddable
public class EmbargoPK implements Serializable {

	private Integer cdg;
	private Date fecha;

	/**
	 * Devuelve el Código de Trabajador
	 * @return
	 */
	@Column(name = "cdg", nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	/**
	 * Devuelve la Fecha Comienzo Embargo
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = false)
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof EmbargoPK))
			return false;
		EmbargoPK castOther = (EmbargoPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& ((this.getFecha() == castOther.getFecha()) || (this
						.getFecha() != null
						&& castOther.getFecha() != null && this.getFecha()
						.equals(castOther.getFecha())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFecha() == null ? 0 : this.getFecha().hashCode());
		return result;
	}
}
