package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.DomainApplicationDB;

@Entity
@Table(name="domain_application")
public class DomainApplication extends DomainApplicationDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public DomainApplication() {
		setActive(true);
		setAuditLevel(AuditLevel.NONE);
	}
	
} 
