package com.code.aon.payroll.resultados.salarios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Paga extra
 */

@Embeddable
public class NominaexPK implements Serializable {

	private Integer cdg;
	private Integer numero;

	/**
	 * Devuelve el Codigo de Trabajador
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
	 * Devuelve el Numero de Paga 
	 * @return
	 */
	@Column(name = "numero", nullable = false, length = 2)
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NominaexPK))
			return false;
		NominaexPK castOther = (NominaexPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& (this.getNumero() == castOther.getNumero());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result + this.getNumero();
		return result;
	}
}
