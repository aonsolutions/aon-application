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
import com.code.aon.config.enumeration.WorkGroupStatus;

@Entity
@Table(name="workgroup")
public class WorkGroup implements ITransferObject {

	private static final long serialVersionUID = 6826723766234882061L;

	private Integer id;
	
	private String description;
	
	private WorkGroupStatus status;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WorkGroupStatus getStatus() {
		return status;
	}

	public void setStatus(WorkGroupStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WorkGroup o = (WorkGroup) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.description, o.description)
				.append(this.status, o.status)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)		
			.append(id)				
			.append(status)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
