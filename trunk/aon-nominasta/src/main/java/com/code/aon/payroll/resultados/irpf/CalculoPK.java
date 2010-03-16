package com.code.aon.payroll.resultados.irpf;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Calculo
 */
@Embeddable
public class CalculoPK implements Serializable {

	private Integer cdg;
	private Integer anio;
	private Integer mes;
	private Integer dia;

	/**
	 * Codigo de Trabajador
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
	 * Dia
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof CalculoPK))
			return false;
		CalculoPK castOther = (CalculoPK) other;

		return (this.getCdg() == castOther.getCdg())
				&& (this.getAnio() == castOther.getAnio())
				&& (this.getMes() == castOther.getMes())
				&& (this.getDia() == castOther.getDia());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCdg();
		result = 37 * result + this.getAnio();
		result = 37 * result + this.getMes();
		result = 37 * result + this.getDia();
		return result;
	}
}
