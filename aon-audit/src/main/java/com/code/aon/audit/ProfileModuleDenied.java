package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProfileModuleDeniedDB;

@Entity
@Table(name="profile_module_denied")
public class ProfileModuleDenied extends ProfileModuleDeniedDB {

	private static final long serialVersionUID = 1L;
	
}
