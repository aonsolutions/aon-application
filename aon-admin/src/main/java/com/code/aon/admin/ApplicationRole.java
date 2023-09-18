package com.code.aon.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ApplicationRoleDB;

@Entity
@Table(name="application_role")
public class ApplicationRole extends ApplicationRoleDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}