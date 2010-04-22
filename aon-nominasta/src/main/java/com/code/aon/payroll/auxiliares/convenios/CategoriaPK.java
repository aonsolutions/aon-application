package com.code.aon.payroll.auxiliares.convenios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Categoria.
 */
@Embeddable
public class CategoriaPK implements Serializable {

	private String codcon;
	private String cdg;

	/**
	 * Devuelve el Codigo de Convenio
	 * 
	 * @return
	 */
	@Column(name = "codcon", nullable = false, length = 2)
	public String getCodcon() {
		return this.codcon;
	}

	public void setCodcon(String codcon) {
		this.codcon = codcon;
	}

	/**
	 * Devuelve el Codigo de Categoria Laboral
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CategoriaPK))
			return false;
		CategoriaPK castOther = (CategoriaPK) other;

		return ((this.getCodcon() == castOther.getCodcon()) || (this
				.getCodcon() != null
				&& castOther.getCodcon() != null && this.getCodcon().equals(
				castOther.getCodcon())))
				&& ((this.getCdg() == castOther.getCdg()) || (this.getCdg() != null
						&& castOther.getCdg() != null && this.getCdg().equals(
						castOther.getCdg())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getCodcon() == null ? 0 : this.getCodcon().hashCode());
		result = 37 * result
				+ (getCdg() == null ? 0 : this.getCdg().hashCode());
		return result;
	}
}
