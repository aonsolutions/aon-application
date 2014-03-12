package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ProfileModuleDeniedDB;

@Entity
@Table(name="profile_module_denied")
public class ProfileModuleDenied extends ProfileModuleDeniedDB implements IModule {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
