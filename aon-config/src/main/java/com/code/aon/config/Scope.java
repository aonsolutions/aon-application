package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ScopeDB;

@Entity
@Table(name="scope")
@Heritable
public class Scope extends ScopeDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
