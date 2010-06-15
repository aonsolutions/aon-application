package com.code.aon.payroll.cotizacion;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Cnae 2009
 */
@Embeddable
public class Cnae2009PK implements Serializable {

	private String cdg;
	private Date fecini;

	@Column(name = "cdg", nullable = false, length = 5)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}
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
		if (!(other instanceof CnaePK))
			return false;
		CnaePK castOther = (CnaePK) other;

		return ((this.getCdg() == castOther.getCdg()) || (this.getCdg() != null
				&& castOther.getCdg() != null && this.getCdg().equals(
				castOther.getCdg())))
				&& ((this.getFecini() == castOther.getFecini()) || (this
						.getFecini() != null
						&& castOther.getFecini() != null && this.getFecini()
						.equals(castOther.getFecini())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCdg() == null ? 0 : this.getCdg().hashCode());
		result = 37 * result
				+ (getFecini() == null ? 0 : this.getFecini().hashCode());
		return result;
	}
}
