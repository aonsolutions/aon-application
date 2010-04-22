package com.esferalia.aon.payroll.cotizacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
	private String descripcion;

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
	public String getDescripcion() {
		return this.descripcion;
	}

	@Override
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TipoBonificacion o = (TipoBonificacion) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.descripcion, o.descripcion)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(descripcion)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
