package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ApplicationUserDB;

@Entity
@Table(name="application_user")
public class ApplicationUser extends ApplicationUserDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public ApplicationUser() {
		setActive(true);
	}

}