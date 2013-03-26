package com.code.aon.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.DomainApplicationModuleDB;

@Entity
@Table(name="domain_application_module")
public class DomainApplicationModule extends DomainApplicationModuleDB implements IModule {

	private static final long serialVersionUID = 1L;
	
}
