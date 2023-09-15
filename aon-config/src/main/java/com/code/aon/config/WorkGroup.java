package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WorkGroupDB;

@Entity
@Table(name="workgroup")
public class WorkGroup extends WorkGroupDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
