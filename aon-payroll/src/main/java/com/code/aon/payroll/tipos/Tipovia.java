package com.code.aon.payroll.tipos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Tipos de Vía
 */
@Entity
@Table(name="tipovia")
public class Tipovia  implements java.io.Serializable {

	private String cdg;
	private String descripcion;

	@Id
	@GeneratedValue
	@Column(name="cdg", unique=true, nullable=false, length=1)
    public String getCdg() {
		return this.cdg;
    }

	public void setCdg(String cdg) {
		this.cdg = cdg;
	}
    
	@Column(name="descripcion", nullable=false, length=60)
	public String getDescripcion() {
		return this.descripcion;
	}
    
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}