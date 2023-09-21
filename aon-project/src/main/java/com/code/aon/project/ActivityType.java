package com.code.aon.project;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ActivityTypeDB;

@Entity
@Table(name="activity_type")
public class ActivityType extends ActivityTypeDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ActivityType() {
		setActive(true);
	}
	
}