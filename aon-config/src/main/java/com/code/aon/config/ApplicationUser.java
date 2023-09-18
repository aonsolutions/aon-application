package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ApplicationUserDB;

@Entity
@Table(name="application_user")
public class ApplicationUser extends ApplicationUserDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ApplicationUser() {
		setActive(true);
	}

}