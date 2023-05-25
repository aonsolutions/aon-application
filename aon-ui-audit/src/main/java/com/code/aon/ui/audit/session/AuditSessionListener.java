/**
 * 
 */
package com.code.aon.ui.audit.session;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.audit.AuditManager;

public class AuditSessionListener implements HttpSessionListener {

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditSessionListener.class);
	
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		LOGGER.info( "Session Created: {}", session.getId() );
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		LOGGER.info( "Session Destroyed: {}", session.getId() );
		closeLoginAudit(session);
	}

	private void closeLoginAudit( HttpSession httpSession ) {
		try {
			AuditManager.closeLoginAudit( httpSession );	
		} catch ( Throwable th ) {
			LOGGER.error( "Error closing login audit", th );
		}
	}	
}
