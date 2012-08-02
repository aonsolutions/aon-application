package com.code.aon.ui.audit.domain;

import java.util.Date;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.domain.DomainEvent;
import com.code.aon.common.domain.IDomainChangeListener;
import com.code.aon.config.Application;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.util.AonUtil;

public class AuditDomainChangeListener implements IDomainChangeListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(AuditDomainChangeListener.class);

	@Override
	public void beforeDomainChanged(DomainEvent event) {
		if (event.getOldDomain() != null) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			Object servletRequest = ec.getRequest();
			if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
				HttpServletRequest request = (HttpServletRequest) servletRequest;
				HttpSession httpSession = request.getSession(false);
				
				closeLoginAudit(httpSession);
			}
		}
	}

	private void closeLoginAudit( HttpSession httpSession ) {
		try {
			AuditManager.closeLoginAudit( httpSession );	
		} catch ( Throwable th ) {
			LOGGER.error( "Error closing login audit", th );
		}
	}	

	@Override
	public void afterDomainChanged(DomainEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object servletRequest = ec.getRequest();
		
		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession httpSession = request.getSession(false);

			if ( httpSession != null) {
				Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
				if ( session == null ) {
					insertLoginAudit(httpSession, request, event.getNewDomain() );
				} else {
					LOGGER.info( "Session already exists {}", session );
				}
			}
		}
	}

	private void insertLoginAudit( HttpSession httpSession, HttpServletRequest request, Integer domain ) {
		try {
			LOGGER.info( "Domain {}", domain );
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			LOGGER.info( "Principal {}", principal );
			Application application = AuditManager.getApplication(request.getContextPath());
			LOGGER.info( "Application {}", application );
			User user = AuditManager.getUser( principal.getUserId() );
			LOGGER.info( "User {}", user );
			AuditLevel level = AuditManager.getAuditLevel(application, domain );
			if ( level != AuditLevel.NONE ) {
				Session session = new Session();
				session.setDomain( domain );
				session.setApplication( application );
				session.setUser( user );
				session.setSessionId( httpSession.getId() );
				session.setStartDate( new Date(httpSession.getCreationTime()) );
				session.setRemoteAddress( request.getRemoteAddr() );
				session.setRemoteHost( request.getRemoteHost() );
				AuditManager.insertSession( httpSession, session, level );				
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error login audit", th );
		}
	}
	
}
