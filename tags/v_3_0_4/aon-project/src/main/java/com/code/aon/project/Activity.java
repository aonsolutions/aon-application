package com.code.aon.project;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
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
	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}
	
	@ManyToOne
	@JoinColumn( name="activity_type",nullable=false )
	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	@ManyToOne
	@JoinColumn( name="workgroup",nullable=false )
	public WorkGroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(WorkGroup workgroup) {
		this.workgroup = workgroup;
	}

	@Override
    public boolean equals(Object obj) {
        if (obj instanceof Activity) {
            return (this.id.equals(((Activity)obj).getId()));
        }
        return false;
    }

}