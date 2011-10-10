package com.code.aon.project;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="project_activity")
public class ProjectActivity implements ITransferObject {

	private static final long serialVersionUID = -4787407277231738055L;

	private Integer id;
	private Project project;
	private ActivityType activityType;
	private boolean active;
	
	public ProjectActivity() {
		this.active = true;
	}
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name="project",nullable=false )
	@ForeignKey(name = "FK_PRJ_ACT_PROJECT")
	@Index(name = "IDX_PRJ_ACT_PROJECT")				
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	@ManyToOne
	@JoinColumn( name="activity_type",nullable=false )
	@ForeignKey(name = "FK_PRJ_ACT_ACTIVITY_TYPE")
	@Index(name = "IDX_PRJ_ACT_ACTIVITY_TYPE")			
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
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
		final ProjectActivity o = (ProjectActivity) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.activityType, o.activityType)
				.append(this.project, o.project)
				.append(this.active, o.active)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id)
			.append(activityType)
			.append(project)
			.append(active)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}