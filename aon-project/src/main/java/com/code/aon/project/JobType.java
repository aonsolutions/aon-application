package com.code.aon.project;


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

/**
 * The Job Type.
 */
@Entity
@Table(name="job_type")
public class JobType implements ITransferObject{
	
	private static final long serialVersionUID = -2573079175301468919L;

	/** The id. */
	private Integer id;
	
	/** The description. */
	private String description;

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
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final JobType o = (JobType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(description).append(id).
			toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}