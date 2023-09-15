package com.code.aon.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.DomainDB;

@Entity
@Table(name="domain")
public class Domain extends DomainDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Domain() {
		super();
		setActive(true);
    }

} 
