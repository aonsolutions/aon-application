package com.code.aon.ui.audit;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.AonNavigationHandler;

public class AuditNavigationHandler extends AonNavigationHandler {

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditNavigationHandler.class);
	
	public AuditNavigationHandler(NavigationHandler base) {
		super( base );
	}	
	
	private void insertActionEntry( HttpSession httpSession, String name ) {
		if ( isActionExecutionAuditEnabled(httpSession) ) {		
			Integer sessionId = AuditManager.getSessionId(httpSession);
			if ( sessionId != null ) {		
				try {
					AuthPrincipal principal = AuditManager.getAuthPrincipal(httpSession);
					Integer domainId = AuditManager.getDomainId(httpSession);
					Integer actionId = AuditManager.getActionId( name, domainId, principal.getApplicationId() );
					AuditManager.createActionEntry(sessionId, domainId, actionId);
				} catch ( Throwable th ) {
					LOGGER.error( "Error in insert action execution", th );
				}
			} else {
				LOGGER.debug( "Not found {} context property", AuditManager.AUDIT_SESSION_PROPERTY  );
			}
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
