package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.UserWorkGroupDB;

@Entity
@Table(name="user_workgroup")
public class UserWorkGroup extends UserWorkGroupDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}