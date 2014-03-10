package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.UserWorkGroupDB;

@Entity
@Table(name="user_workgroup")
public class UserWorkGroup extends UserWorkGroupDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}