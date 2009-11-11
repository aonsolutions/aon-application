/**
 * 
 */
package com.code.aon.ui.audit.session;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.code.aon.audit.Session;
import com.code.aon.ui.audit.AuditManager;

public class AuditSessionListener implements HttpSessionListener {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AuditSessionListener.class.getName());

	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		LOGGER.info( "Session Created: " + session.getId() );
		AuditManager manager = AuditManager.getInstance();
		if (! manager.isAuditConfigured() ) {
			manager.configureAudit();
		}
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		LOGGER.info( "Session Destroyed: " + session.getId() );
		closeLoginAudit(session);
	}

	private void closeLoginAudit( HttpSession httpSession ) {
		AuditManager manager = AuditManager.getInstance();
		try {
			Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
			if ( session != null ) {
				manager.closeLoginAudit(session);	
			}
		} catch ( Throwable th ) {
			LOGGER.log( Level.SEVERE, "Error closing login audit", th );
		}
	}	
}
