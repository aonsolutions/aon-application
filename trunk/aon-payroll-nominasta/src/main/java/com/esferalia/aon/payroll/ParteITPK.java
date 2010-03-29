package com.esferalia.aon.payroll;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class ParteITPK implements Serializable {

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
		if (o.getCdg() == null && getCdg() == null) {
			return new EqualsBuilder()
				.append(this.fechaBaja, o.fechaBaja)
				.isEquals();
		}
		return ObjectUtils.equals(getCdg(), o.getCdg()) && ObjectUtils.equals(getFechaBaja(), o.getFechaBaja());		
	}
	
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder().append(cdg).append(fechaBaja).hashCode();
//	}
	
}
