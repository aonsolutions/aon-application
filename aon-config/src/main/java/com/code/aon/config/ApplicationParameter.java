package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "app_param")
public class ApplicationParameter implements ITransferObject {

	private static final long serialVersionUID = -7241719325009628022L;
	
	private Integer id;
	private String name;
	private String value;
	private String defaultValue;
	
	private boolean systemParameter = false;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	@Column(length = 32, nullable = false)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 64)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Transient
	public boolean isSystemParameter() {
		return systemParameter;
	}
	public void setSystemParameter(boolean systemParameter) {
		this.systemParameter = systemParameter;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ApplicationParameter o = (ApplicationParameter) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.value, o.value)
				.isEquals();
		}
		return ObjectUtils.equals(getName(), o.getName());		
	}	

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(name)
			.append(value)
			.toHashCode();
	}	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}