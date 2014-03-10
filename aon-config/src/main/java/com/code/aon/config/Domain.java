package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.esferalia.aon.entity.master.DomainDB;

@Entity
@Table(name="domain")
public class Domain extends DomainDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Domain() {
		super();
		setActive(true);
    }

} 
