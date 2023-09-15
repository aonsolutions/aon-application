package com.code.aon.commercial;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.AonVersion;
import com.code.aon.project.IProject;
import com.esferalia.aon.entity.master.ProjectCommercialDB;

@Entity
@Table(name="project_commercial")
public class ProjectCommercial extends ProjectCommercialDB implements IProject{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<CommercialTracking> trackings = new HashSet<CommercialTracking>();
	
	public ProjectCommercial() {
		setStatus(ProjectStatus.PENDING);
	}

	@OneToMany(mappedBy = "project", cascade={CascadeType.REMOVE})
	public Set<CommercialTracking> getTrackings() {
		return trackings;
	}

	public void setTrackings(Set<CommercialTracking> trackings) {
		this.trackings = trackings;
	}
	
}