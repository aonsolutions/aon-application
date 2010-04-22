package com.code.aon.payroll.resultados.irpf;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * LinImpresos190PK
 */
@Embeddable
public class LinImpresos190PK implements Serializable {

	private Integer cdg;
	private Integer linea;

	/**
	 * Codigo de 190
	 * 
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
	 * Numero de Linea
	 * 
	 * @return
	 */
	@Column(name = "linea", nullable = false, length = 2)
	public Integer getLinea() {
		return this.linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof LinImpresos190PK))
			return false;
		LinImpresos190PK castOther = (LinImpresos190PK) other;

		return (this.getCdg() == castOther.getCdg())
				&& (this.getLinea() == castOther.getLinea());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result + this.getLinea();
		return result;
	}
}
