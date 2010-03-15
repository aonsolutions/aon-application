package com.code.aon.payroll.resultados.salarios;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Nomdtoex
 */

@Embeddable
public class NomdtoexPK implements Serializable {

	private int cdg;
	private int numero;
	private int linea;

	/**
	 * Codigo de Trabajador
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
	 * Numero de Paga
	 * @return
	 */
	@Column(name = "numero", nullable = false, length = 2)
	public int getNumero() {
		return this.numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	/**
	 * Numero de Orden
	 * @return
	 */
	@Column(name = "linea", nullable = false, length = 2)
	public int getLinea() {
		return this.linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NomdtoexPK))
			return false;
		NomdtoexPK castOther = (NomdtoexPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& (this.getNumero() == castOther.getNumero())
				&& (this.getLinea() == castOther.getLinea());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result + this.getNumero();
		result = 37 * result + this.getLinea();
		return result;
	}
}
