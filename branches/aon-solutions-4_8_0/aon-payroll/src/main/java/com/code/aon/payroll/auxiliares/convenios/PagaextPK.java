package com.code.aon.payroll.auxiliares.convenios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Pagaext.
 */
@Embeddable
public class PagaextPK implements Serializable {

	private String cdg;
	private String codcom;

	/**
	 * Codigo de Convenio
	 * 
	 * @return
	 */
	@Column(name = "cdg", nullable = false, length = 2)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
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
		if (!(other instanceof PagaextPK))
			return false;
		PagaextPK castOther = (PagaextPK) other;

		return ((this.getCdg() == castOther.getCdg()) || (this.getCdg() != null
				&& castOther.getCdg() != null && this.getCdg().equals(
				castOther.getCdg())))
				&& ((this.getCodcom() == castOther.getCodcom()) || (this
						.getCodcom() != null
						&& castOther.getCodcom() != null && this.getCodcom()
						.equals(castOther.getCodcom())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCdg() == null ? 0 : this.getCdg().hashCode());
		result = 37 * result
				+ (getCodcom() == null ? 0 : this.getCodcom().hashCode());
		return result;
	}
}
