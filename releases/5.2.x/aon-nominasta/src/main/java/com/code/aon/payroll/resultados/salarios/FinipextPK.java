package com.code.aon.payroll.resultados.salarios;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Clave primaria de Finipext
 */
@Embeddable
public class FinipextPK implements Serializable {

	private int cdg;
	private Date fecini;
	private String codcom;

	/**
	 * Codigo de Finiquito
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
	 * Inicio Devengo Paga Extra
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
	 * Codigo de Complemento
	 * 
	 * @return
	 */
	@Column(name = "codcom", nullable = false, length = 2)
	public String getCodcom() {
		return this.codcom;
	}

	public void setCodcom(String codcom) {
		this.codcom = codcom;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof FinipextPK))
			return false;
		FinipextPK castOther = (FinipextPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& ((this.getFecini() == castOther.getFecini()) || (this
						.getFecini() != null
						&& castOther.getFecini() != null && this.getFecini()
						.equals(castOther.getFecini())))
				&& ((this.getCodcom() == castOther.getCodcom()) || (this
						.getCodcom() != null
						&& castOther.getCodcom() != null && this.getCodcom()
						.equals(castOther.getCodcom())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result
				+ (getFecini() == null ? 0 : this.getFecini().hashCode());
		result = 37 * result
				+ (getCodcom() == null ? 0 : this.getCodcom().hashCode());
		return result;
	}
}
