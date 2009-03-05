package com.code.aon.payroll.principales.personas;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Nominait
 */
@Embeddable
public class NominaitPK implements Serializable {

	private int cdg;
	private Date feciniit;
	private Date fecini;

	// @DataDefinition(label="Codigo de Trabajador")
	@Column(name = "cdg", nullable = false, length = 4)
	public int getCdg() {
		return this.cdg;
	}

	public void setCdg(int cdg) {
		this.cdg = cdg;
	}

	// @DataDefinition(label="Fecha Inicio Incidencia")
	@Temporal(TemporalType.DATE)
	@Column(name = "feciniit", nullable = false)
	public Date getFeciniit() {
		return this.feciniit;
	}

	public void setFeciniit(Date feciniit) {
		this.feciniit = feciniit;
	}

	// @DataDefinition(label="Fecha Inicio Incidencia Periodo Nomina")
	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NominaitPK))
			return false;
		NominaitPK castOther = (NominaitPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& ((this.getFeciniit() == castOther.getFeciniit()) || (this
						.getFeciniit() != null
						&& castOther.getFeciniit() != null && this
						.getFeciniit().equals(castOther.getFeciniit())))
				&& ((this.getFecini() == castOther.getFecini()) || (this
						.getFecini() != null
						&& castOther.getFecini() != null && this.getFecini()
						.equals(castOther.getFecini())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFeciniit() == null ? 0 : this.getFeciniit().hashCode());
		result = 37 * result
				+ (getFecini() == null ? 0 : this.getFecini().hashCode());
		return result;
	}
}
