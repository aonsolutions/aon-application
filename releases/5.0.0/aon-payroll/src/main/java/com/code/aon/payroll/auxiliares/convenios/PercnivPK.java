package com.code.aon.payroll.auxiliares.convenios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Categoria.
 */
@Embeddable
public class PercnivPK implements Serializable {

	private String cdg;
	private String nivel;
	private String codcom;

	/**
	 * Devuelve el Codigo de Convenio
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
	 * Devuelve el Codigo de Nivel Retributivo
	 * 
	 * @return
	 */
	@Column(name = "nivel", nullable = false, length = 2)
	public String getNivel() {
		return this.nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * Devuelve el Codigo de Complemento
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
		if (!(other instanceof PercnivPK))
			return false;
		PercnivPK castOther = (PercnivPK) other;

		return ((this.getCdg() == castOther.getCdg()) || (this.getCdg() != null
				&& castOther.getCdg() != null && this.getCdg().equals(
				castOther.getCdg())))
				&& ((this.getNivel() == castOther.getNivel()) || (this
						.getNivel() != null
						&& castOther.getNivel() != null && this.getNivel()
						.equals(castOther.getNivel())))
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
				+ (getNivel() == null ? 0 : this.getNivel().hashCode());
		result = 37 * result
				+ (getCodcom() == null ? 0 : this.getCodcom().hashCode());
		return result;
	}
}
