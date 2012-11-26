package com.code.aon.common.audit;

import java.security.Principal;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AuditableBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	public static final String SECURITY_SUBJECT = "java:comp/env/security/subject";
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditableBeanVetoListener.class);
	
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
		try {
			InitialContext ic = new InitialContext();
			Subject subject = (Subject) ic.lookup(SECURITY_SUBJECT);
			Principal principal = subject.getPrincipals().iterator().next();
			String login = principal!=null?principal.getName():null;
			login = StringUtils.substringBefore(login, "@");
			return login;
		} catch (NamingException e) {
			LOGGER.warn("No se pudo identificar el usuario conectado");
		}
		return null;
	}

}
