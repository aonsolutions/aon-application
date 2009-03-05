package com.code.aon.payroll.principales.personas;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Prcdivtrab
 */
@Embeddable
public class PrcdivtrabPK implements Serializable {

	private int cdg;
	private Date fecini;
	private int orden;

	// @DataDefinition(label="Codigo de Trabajador")
	@Column(name = "cdg", nullable = false, length = 4)
	public int getCdg() {
		return this.cdg;
	}

	public void setCdg(int cdg) {
		this.cdg = cdg;
	}

	// @DataDefinition(label="Fecha Inicio")
	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	// @DataDefinition(label="Orden")
	@Column(name = "orden", nullable = false, length = 2)
	public int getOrden() {
		return this.orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PrcdivtrabPK))
			return false;
		PrcdivtrabPK castOther = (PrcdivtrabPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& ((this.getFecini() == castOther.getFecini()) || (this
						.getFecini() != null
						&& castOther.getFecini() != null && this.getFecini()
						.equals(castOther.getFecini())))
				&& (this.getOrden() == castOther.getOrden());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFecini() == null ? 0 : this.getFecini().hashCode());
		result = 37 * result + this.getOrden();
		return result;
	}
}
