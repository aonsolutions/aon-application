package com.code.aon.admin;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.RoleDB;

@Entity
@Table(name="role")
public class Role extends RoleDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}