package com.code.aon.ui.audit;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.ui.common.AonNavigationHandler;

public class AuditNavigationHandler extends AonNavigationHandler {

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditNavigationHandler.class);
	
	public AuditNavigationHandler(NavigationHandler base) {
		super( base );
	}	
	
	private void insertActionEntry( HttpSession httpSession, String name ) {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {		
			try {
				Action action = AuditManager.getAction( name, session.getApplication() );
				if ( isActionExecutionAuditEnabled(httpSession) ) {
					AuditManager.createActionEntry(session, action);
				}
			} catch ( Throwable th ) {
				LOGGER.error( "Error in insert action execution", th );
			}
		} else {
			LOGGER.error( "Not found {}", AuditManager.AUDIT_SESSION_PROPERTY  );
		}
	}	
	
	private boolean isActionExecutionAuditEnabled( HttpSession httpSession ) {
		AuditLevel level = (AuditLevel) httpSession.getAttribute( AuditManager.AUDIT_LEVEL_PROPERTY );
		return level == AuditLevel.MODULE;
	}

	@Override
	protected void process(FacesContext fc, String fromAction, String outcome) {
    	HttpSession httpSession = (HttpSession) fc.getExternalContext().getSession(false);
    	if ( httpSession != null ) {
    		insertActionEntry(httpSession, outcome);	
    	}	
	}

}
