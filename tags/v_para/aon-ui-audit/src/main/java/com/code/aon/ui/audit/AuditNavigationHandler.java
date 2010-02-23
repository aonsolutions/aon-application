package com.code.aon.ui.audit;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;

public class AuditNavigationHandler extends NavigationHandler {

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditNavigationHandler.class);
	
	private NavigationHandler _base;
	
	public AuditNavigationHandler(NavigationHandler base) {
		_base = base;
	}	
	
	private void insertActionEntry( HttpSession httpSession, String name ) {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {		
			AuditManager manager = AuditManager.getInstance();
			try {
				Action action = manager.getAction( name, session.getApplication() );
				if ( isActionExecutionAuditEnabled(httpSession) ) {
					manager.createActionEntry(session, action);
				}
			} catch ( Throwable th ) {
				LOGGER.error( "Error in insert action execution", th );
			}
		}
	}	
	
	private boolean isActionExecutionAuditEnabled( HttpSession httpSession ) {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {
			return session.getApplication().getAuditLevel() == AuditLevel.MODULE;
		}
		return false;
	}
	
	@Override
	public void handleNavigation(FacesContext fc, String actionMethodCurrent, String actionNameCurrent) {
		if (! StringUtils.isEmpty(actionNameCurrent) ) {
	    	HttpSession httpSession = (HttpSession) fc.getExternalContext().getSession(false);
	    	if ( httpSession != null ) {
	    		insertActionEntry(httpSession, actionNameCurrent);	
	    	}	
		}
		_base.handleNavigation(fc, actionMethodCurrent, actionNameCurrent);
	}

	

}
