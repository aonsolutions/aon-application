package com.esferalia.aon.payroll.cotizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

/**
 * Tipos de Bonificacion
 */
@Entity
@Table(name = "tipboni")
public class TipoBonificacion implements ITransferObject, ITipoBonificacion {

	private static final long serialVersionUID = 3418444674669580634L;
	
	private Integer id;
	private String description;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 2)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "descripcion", nullable = false, length = 50)
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
