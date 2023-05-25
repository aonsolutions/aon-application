package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ApplicationDB;

@Entity
@Table(name="application")
public class Application extends ApplicationDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
