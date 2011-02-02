package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.config.enumeration.PayMethodType;

@Entity
@Table(name = "pm_type_detail")
public class PayMethodTypeDetail implements ITransferObject{

	private static final long serialVersionUID = 5028067423703067850L;

	private Integer id;
	private String description;
	private PayMethodType type;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "description", length = 32, nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "type", nullable = false)
	public PayMethodType getType() {
		return type;
	}

	public void setType(PayMethodType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final PayMethodTypeDetail o = (PayMethodTypeDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.type, o.type)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(description)
			.append(type)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
