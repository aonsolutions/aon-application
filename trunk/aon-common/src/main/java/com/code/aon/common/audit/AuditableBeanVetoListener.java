package com.code.aon.common.audit;

import java.util.Date;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.jaas.auth.AuthPrincipal;

public class AuditableBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditableBeanVetoListener.class);
	
	private IAuthPrincipalProvider authPrincipalProvider;
	
	@SuppressWarnings("unchecked")
	public IAuthPrincipalProvider getAuthPrincipalProvider() {
		if ( authPrincipalProvider == null ) {
			String className = System.getProperty(IAuditable.AUTH_PRINCIPAL_PROVIDER);
			try {
				Class<IAuthPrincipalProvider> _class = ClassUtils.getClass(className);
				authPrincipalProvider = _class.newInstance();
			} catch (Throwable th) {
				LOGGER.error( "Error creating AuthPrincipalProvider " + className, th );
			}
		}
		return authPrincipalProvider;
	}

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IAuditable pojo = (IAuditable) evt.getTo();
		pojo.setCreationUser( getLoggedUser() );
		pojo.setCreationDate( new Date()  );
	}
	
	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IAuditable pojo = (IAuditable) evt.getTo();
		pojo.setModificationUser( getLoggedUser() );
		pojo.setModificationDate( new Date()  );
	}

	private String getLoggedUser() {
		if ( getAuthPrincipalProvider() != null ) {
			AuthPrincipal principal = getAuthPrincipalProvider().getAuthPrincipal();
			if ( principal != null ) {
				return principal.getShortName();
			}
		}
		LOGGER.warn("No se pudo identificar el usuario conectado");			
		return null;
	}

}
