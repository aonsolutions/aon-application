package com.esferalia.aon.payroll;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class PercepcionPK implements Serializable, Comparable<PercepcionPK> {

	private static final long serialVersionUID = -779490619293949385L;

	private Integer cdg;
	private Integer numero;

	@Column(name = "cdg", nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}
	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	@Column(name = "numero", nullable = false, length = 4)
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero; 
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final PercepcionPK o = (PercepcionPK) obj;
		return new EqualsBuilder()
			.append(this.cdg, o.cdg)
			.append(this.numero, o.numero)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public int compareTo(PercepcionPK o) {
		PercepcionPK myClass = (PercepcionPK) o;
	     return new CompareToBuilder()
	       .append(this.cdg, myClass.cdg)
	       .append(this.numero, myClass.numero)
	       .toComparison();
   }

}
