package com.code.aon.payroll.avanzadas.kartel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Percepcion
 */
@Entity
@Table(name = "percepcion")
public class Percepcion implements ITransferObject {

	private String cdg;
	private String descripcion;
	private String tipo;

	/**
	 * Devuelve el Código Percepción
	 */
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 8)
	public String getCdg() {
		return this.cdg;
	}

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	/**
	 * Devuelve el Descripción Percepción 
	 * @return
	 */
	@Column(name = "descripcion", length = 35)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Devuelve el Tipo Percepción
	 * @return
	 */
	@Column(name = "tipo", nullable = false, length = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
