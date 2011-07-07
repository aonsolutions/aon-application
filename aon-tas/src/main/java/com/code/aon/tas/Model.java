package com.code.aon.tas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "model")
public class Model implements ITransferObject{

	private static final long serialVersionUID = 5608358274994999663L;
	
	private Integer id;
	private Make make;
	private String name;

	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "make", nullable = false)
	public Make getMake() {
		return this.make;
	}
	public void setMake(Make make) {
		this.make = make;
	}

	@Column(length = 64, nullable = false)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getFullName() {
		return ((getMake() != null) ? getMake().getFullName() + " " : "") + ((getName() != null) ? getName() : "");
	}
	public void setFullName(String value) {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Model o = (Model) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)								
				.append(this.make, o.make)								
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()		
			.append(id)								
			.append(name)						
			.append(make)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return PojoToStringBuilder.reflectionToString(this);
	}

}
