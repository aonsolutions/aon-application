package com.code.aon.project;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;

@Entity
@Table(name="project_type")
public class ProjectType implements ITransferObject, IEnterprise {

	private static final long serialVersionUID = 4694408011382916566L;

	private Integer id;
	private Enterprise enterprise;
	private String description;
	private boolean active;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_PROJECT_TYPE_ENTERPRISE")
    @Index(name = "IDX_PROJECT_TYPE_ENTERPRISE")    
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

	@Column(length=64, nullable=false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectType o = (ProjectType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)
				.append(this.description, o.description)
				.append(this.active, o.active)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id)
			.append(enterprise)
			.append(description)
			.append(active).
			toHashCode();
	}
	@Override
	public String toString() {
		return PojoToStringBuilder.reflectionToString(this);
	}
	
}
