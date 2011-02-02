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
import com.code.aon.config.WorkGroup;

@Entity
@Table(name="activity")
public class Activity implements ITransferObject {

	private static final long serialVersionUID = -4787407277231738055L;

	private Integer id;
	
	private Dossier dossier;
	
	private ActivityType activityType;
	
	private WorkGroup workgroup;
	
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
	@JoinColumn( name="dossier",nullable=false )
	@ForeignKey(name = "FK_ACTIVITY_DOSSIER")
	@Index(name = "IDX_ACTIVITY_DOSSIER")				
	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}
	
	@ManyToOne
	@JoinColumn( name="activity_type",nullable=false )
	@ForeignKey(name = "FK_ACTIVITY_ACTIVITY_TYPE")
	@Index(name = "IDX_ACTIVITY_ACTIVITY_TYPE")			
	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	@ManyToOne
	@JoinColumn( name="workgroup",nullable=false )
	@ForeignKey(name = "FK_ACTIVITY_WORKGROUP")
	@Index(name = "IDX_ACTIVITY_WORKGROUP")		
	public WorkGroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(WorkGroup workgroup) {
		this.workgroup = workgroup;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Activity o = (Activity) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.activityType, o.activityType)
				.append(this.dossier, o.dossier)
				.append(this.workgroup, o.workgroup)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(activityType).append(dossier).
			append(id).append(workgroup).
			toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}