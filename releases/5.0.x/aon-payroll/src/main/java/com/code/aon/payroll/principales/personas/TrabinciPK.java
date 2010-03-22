package com.code.aon.payroll.principales.personas;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Trabinci
 */
@Embeddable
public class TrabinciPK implements Serializable {

	private int cdg;
	private Date fecini;
	private String codinc;

	/**
	 * Codigo de Trabajador
	 * 
	 * @return
	 */
	@Column(name = "cdg", nullable = false, length = 4)
	public int getCdg() {
		return this.cdg;
	}

	public void setCdg(int cdg) {
		this.cdg = cdg;
	}

	/**
	 * Fecha Inicio Incidencia
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	/**
	 * Tipo de Incidencia
	 * 
	 * @return
	 */
	@Column(name = "codinc", nullable = false, length = 10)
	public String getCodinc() {
		return this.codinc;
	}

	public void setCodinc(String codinc) {
		this.codinc = codinc;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TrabinciPK))
			return false;
		TrabinciPK castOther = (TrabinciPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& ((this.getFecini() == castOther.getFecini()) || (this
						.getFecini() != null
						&& castOther.getFecini() != null && this.getFecini()
						.equals(castOther.getFecini())))
				&& ((this.getCodinc() == castOther.getCodinc()) || (this
						.getCodinc() != null
						&& castOther.getCodinc() != null && this.getCodinc()
						.equals(castOther.getCodinc())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFecini() == null ? 0 : this.getFecini().hashCode());
		result = 37 * result
				+ (getCodinc() == null ? 0 : this.getCodinc().hashCode());
		return result;
	}
}
