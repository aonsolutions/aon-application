package com.code.aon.ui.audit;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.code.aon.audit.Action;
import com.code.aon.audit.DomainApplication;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;

public class AuditNavigationHandler extends NavigationHandler {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AuditNavigationHandler.class.getName());	
	
	private NavigationHandler _base;
	
	public AuditNavigationHandler(NavigationHandler base) {
		_base = base;
	}	
	
	private void insertActionExecution( HttpSession httpSession, String name ) {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {		
			AuditManager manager = AuditManager.getInstance();
			try {
				Action action = manager.getAction( name, session.getApplication() );
				if ( isActionExecutionAuditEnabled(httpSession) ) {
					manager.createActionExecution(session, action);
				}
			} catch ( Throwable th ) {
				LOGGER.log( Level.SEVERE, "Error in insert action execution", th );
			}
		}
	}	
	
	private boolean isActionExecutionAuditEnabled( HttpSession httpSession ) {
		DomainApplication da = (DomainApplication) httpSession.getAttribute( AuditManager.AUDIT_DOMAIN_APPLICATION_PROPERTY );
		if ( da != null ) {
			return da.getAuditLevel() == AuditLevel.MODULE;
		}
		return false;
	}
	
	@Override
	public void handleNavigation(FacesContext fc, String actionMethodCurrent, String actionNameCurrent) {
		if (! StringUtils.isEmpty(actionNameCurrent) ) {
	    	HttpSession httpSession = (HttpSession) fc.getExternalContext().getSession(false);
	    	if ( httpSession != null ) {
	    		insertActionExecution(httpSession, actionNameCurrent);	
	    	}	
		}
		_base.handleNavigation(fc, actionMethodCurrent, actionNameCurrent);
	}

	

}
