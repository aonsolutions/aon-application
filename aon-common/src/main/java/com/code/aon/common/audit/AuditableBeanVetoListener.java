package com.code.aon.common.audit;

import java.util.Date;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ICommonConstants;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.jaas.auth.AuthPrincipal;

public class AuditableBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditableBeanVetoListener.class);
	
	private IAuthPrincipalProvider authPrincipalProvider;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IAuditable pojo = (IAuditable)evt.getTo();
		if (pojo.getCreationUser() == null || !pojo.getCreationUser().equals(ICommonConstants.SYSTEM_USER)) {
			String loggedUser = getLoggedUser();
			if (loggedUser != null) {
				pojo.setCreationUser(loggedUser);
			}
		}
		pojo.setCreationDate(new Date());
		pojo.setModificationUser(null);
		pojo.setModificationDate(null);
	}
	
	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IAuditable pojo = (IAuditable)evt.getTo();
		String loggedUser = getLoggedUser();
		if (loggedUser != null) {
			pojo.setModificationUser(loggedUser);
		}
		pojo.setModificationDate(new Date());
	}

	private String getLoggedUser() {
		if (getAuthPrincipalProvider() != null) {
			AuthPrincipal principal = getAuthPrincipalProvider().getAuthPrincipal();
			if (principal != null) {
				return principal.getShortName();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public IAuthPrincipalProvider getAuthPrincipalProvider() {
		if (authPrincipalProvider == null) {
			String className = System.getProperty(IAuditable.AUTH_PRINCIPAL_PROVIDER);
			try {
				Class<IAuthPrincipalProvider> _class = ClassUtils.getClass(className);
				authPrincipalProvider = _class.newInstance();
			} catch (Throwable th) {
				LOGGER.error("Error creating AuthPrincipalProvider " + className, th);
			}
		}
		return authPrincipalProvider;
	}

}
