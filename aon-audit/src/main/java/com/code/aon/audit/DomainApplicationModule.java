package com.code.aon.audit;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.DomainApplicationModuleDB;

@Entity
@Table(name="domain_application_module")
public class DomainApplicationModule extends DomainApplicationModuleDB implements IModule {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
