package com.code.aon.project;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProjectDB;

@Entity
@Table(name="project")
public class Project extends ProjectDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<ProjectActivity> activities = new HashSet<ProjectActivity>();
	private Set<ProjectAttachment> attachments = new HashSet<ProjectAttachment>();

	public Project() {
		setDate(new Date());
		setActive(true);
	}

	@Transient
	public boolean isExtended() {
		return isCommercial() || isTas() || isReservation();
	}
	
	@OneToMany(mappedBy = "project", cascade={CascadeType.REMOVE})
	public Set<ProjectAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<ProjectAttachment> attachments) {
		this.attachments = attachments;
	}

	@OneToMany(mappedBy = "project", cascade={CascadeType.REMOVE})
	public Set<ProjectActivity> getActivities() {
		return activities;
	}

	public void setActivities(Set<ProjectActivity> activities) {
		this.activities = activities;
	}
	
}
