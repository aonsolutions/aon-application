package com.code.aon.admin;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProfileRoleDB;

@Entity
@Table(name="profile_role")
public class ProfileRole extends ProfileRoleDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}