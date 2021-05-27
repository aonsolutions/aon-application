package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.UserScopeDB;

@Entity
@Table(name="user_scope")
public class UserScope extends UserScopeDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}