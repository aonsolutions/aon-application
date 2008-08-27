package com.code.aon.ui.audit;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;

public class AuditSessionFactoryNameProvider implements ISessionFactoryNameProvider {
	
	private static final String AUDIT = "aon-audit";

	private String name;
	
	public AuditSessionFactoryNameProvider() {
		String currentName = HibernateUtil.getSessionFactoryName();
		int pos = currentName.indexOf( "/" );
		this.name = ( pos != -1 ) ? currentName.substring(0, pos+1) + AUDIT : AUDIT; 
	}

	@Override
	public String getName() {
		return name;
	}

}
