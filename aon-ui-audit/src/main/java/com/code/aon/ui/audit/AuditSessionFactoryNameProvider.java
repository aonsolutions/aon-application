package com.code.aon.ui.audit;

import org.apache.commons.lang.ClassUtils;

import com.code.aon.audit.Domain;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;

public class AuditSessionFactoryNameProvider implements ISessionFactoryNameProvider {
	
	private static final String AUDIT_PACKAGE = ClassUtils.getPackageName(Domain.class);
	
	private ISessionFactoryNameProvider defaultProvider;
	
	public AuditSessionFactoryNameProvider( ISessionFactoryNameProvider defaultProvider ) {
		this.defaultProvider = defaultProvider;
	}

	@Override
	public String getName( String pojoClass ) {
		String _package = ClassUtils.getPackageName(pojoClass);
		String name = defaultProvider.getName(pojoClass);
		if ( AUDIT_PACKAGE.equals(_package) ) {
			int pos = name.indexOf( "/" );
			name = ( pos != -1 ) ? name.substring(0, pos+1) + AuditManager.AUDIT : AuditManager.AUDIT; 			
		}
		return name;
	}

}
