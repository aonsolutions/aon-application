package com.code.aon.payroll.resultados.irpf;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Lincalculo
 */
@Embeddable
public class LincalcuPK implements Serializable {

	private Integer numero;
	private Integer anio;
	private Integer mes;
	private Integer dia;
	private Integer linea;

	/**
	 * Codigo de Trabajador
	 * 
	 * @return
	 */
	@Column(name = "numero", nullable = false, length = 4)
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/**
	 * Anio
	 * 
	 * @return
	 */
	@Column(name = "anio", nullable = false, length = 2)
	public Integer getAnio() {
		return this.anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	/**
	 * Mes
	 * 
	 * @return
	 */
	@Column(name = "mes", nullable = false, length = 2)
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	/**
	 * Dia de calculo de I.R.P.F.
	 * 
	 * @return
	 */
	@Column(name = "dia", nullable = false, length = 2)
	public Integer getDia() {
		return this.dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	/**
	 * Linea de elemento
	 * 
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
		if (!(other instanceof LincalcuPK))
			return false;
		LincalcuPK castOther = (LincalcuPK) other;

		return (this.getNumero() == castOther.getNumero())
				&& (this.getAnio() == castOther.getAnio())
				&& (this.getMes() == castOther.getMes())
				&& (this.getDia() == castOther.getDia())
				&& (this.getLinea() == castOther.getLinea());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getNumero();
		result = 37 * result + this.getAnio();
		result = 37 * result + this.getMes();
		result = 37 * result + this.getDia();
		result = 37 * result + this.getLinea();
		return result;
	}
}
