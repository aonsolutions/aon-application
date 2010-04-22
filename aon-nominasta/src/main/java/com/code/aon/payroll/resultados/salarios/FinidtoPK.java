package com.code.aon.payroll.resultados.salarios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Finidto
 */
@Embeddable
public class FinidtoPK implements Serializable {

	private int cdg;
	private int orden;

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
	 * Orden
	 * 
	 * @return
	 */
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
		if (!(other instanceof FinidtoPK))
			return false;
		FinidtoPK castOther = (FinidtoPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& (this.getOrden() == castOther.getOrden());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result + this.getOrden();
		return result;
	}
}
