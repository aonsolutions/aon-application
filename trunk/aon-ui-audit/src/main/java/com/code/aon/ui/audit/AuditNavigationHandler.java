package com.code.aon.ui.audit;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.code.aon.audit.Action;
import com.code.aon.audit.Session;

public class AuditNavigationHandler extends NavigationHandler {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AuditNavigationHandler.class.getName());	
	
	private NavigationHandler _base;
	
	public AuditNavigationHandler(NavigationHandler base) {
		_base = base;
	}	
	
	private void insertActionExecution( HttpSession httpSession, String name ) {
		AuditManager manager = AuditManager.getInstance();
		manager.changeToAuditDB();
		try {
			Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
			Action action = manager.getAction( name, session.getApplication() );
			manager.createActionExecution(session, action);
		} catch ( Throwable th ) {
			LOGGER.log( Level.SEVERE, "Error in insert action execution", th );
		} finally {
			manager.restoreToPreviousDB();	
		}
	}	
	
	@Override
	public void handleNavigation(FacesContext fc, String actionMethodCurrent, String actionNameCurrent) {
		if (! StringUtils.isEmpty(actionNameCurrent) ) {
	    	HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
	    	insertActionExecution(session, actionNameCurrent);
		}
		_base.handleNavigation(fc, actionMethodCurrent, actionNameCurrent);
	}

	

}
