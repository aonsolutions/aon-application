package com.code.aon.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ApplicationUserProfileDB;

@Entity
@Table(name="application_user_profile")
public class ApplicationUserProfile extends ApplicationUserProfileDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}