package com.code.aon.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ProfileModuleDeniedDB;

@Entity
@Table(name="profile_module_denied")
public class ProfileModuleDenied extends ProfileModuleDeniedDB implements IModule {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
