package com.esferalia.aon.payroll;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class ParteITPK implements Serializable, Comparable<ParteITPK> {

	private static final long serialVersionUID = -775104683033314551L;
	
	private Integer cdg;
	private Date fechaBaja;

	@Column(name = "cdg", nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}
	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	public Date getFechaBaja() {
		return fechaBaja;
	}
	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja; 
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
		final ParteITPK o = (ParteITPK) obj;
		return new EqualsBuilder()
			.append(this.cdg, o.cdg)
			.append(this.fechaBaja, o.fechaBaja)
			.isEquals();
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public int compareTo(ParteITPK o) {
		ParteITPK myClass = (ParteITPK) o;
	     return new CompareToBuilder()
	       .append(this.cdg, myClass.cdg)
	       .append(this.fechaBaja, myClass.fechaBaja)
	       .toComparison();
   }

}
