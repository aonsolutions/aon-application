package com.code.aon.project;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProjectActivityDB;

@Entity
@Table(name="project_activity")
public class ProjectActivity extends ProjectActivityDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ProjectActivity() {
		setActive(true);
	}
	
}