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
import com.code.aon.config.enumeration.PayMethodType;

/**
 * Transfer Object that represents an PayMethod.
 */
@Entity
@Table(name = "pay_method")
public class PayMethod implements ITransferObject{
	
	private static final long serialVersionUID = 6017204177473343703L;

	/** The id. */
	private Integer id;
	
	/** The name. */
	private String name;
	
	/** The type of pay method. */
	private PayMethodType type;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param primaryKey the primary key
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(name = "name", length = 32, nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type of pay method.
	 * 
	 * @return the type
	 */
	@Column(name = "type", nullable = false)
	public PayMethodType getType() {
		return type;
	}

	/**
	 * Sets the type of pay method.
	 * 
	 * @param type the type
	 */
	public void setType(PayMethodType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final PayMethod o = (PayMethod) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.type, o.type)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(name)
			.append(type)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
