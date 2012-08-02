package com.code.aon.ui.audit.session;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.config.Application;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.util.AonUtil;

public class AuditSessionFilter implements Filter {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditSessionFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	private void insertLoginAudit( HttpSession httpSession, HttpServletRequest request ) {
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			LOGGER.info( "Principal {}", principal );
			Application application = AuditManager.getApplication(request.getContextPath());
			LOGGER.info( "Application {}", application );
			User user = AuditManager.getUser( principal.getUserId() );
			if ( user != null ) {
				LOGGER.info( "User {}", user );
				AuditLevel level = AuditManager.getAuditLevel(application, principal.getDomainId() );
				if ( level != AuditLevel.NONE ) {
					Session session = new Session();
					session.setDomain(principal.getDomainId());
					session.setApplication( application );
					session.setUser( user );
					session.setSessionId( httpSession.getId() );
					session.setStartDate( new Date(httpSession.getCreationTime()) );
					session.setRemoteAddress( request.getRemoteAddr() );
					session.setRemoteHost( request.getRemoteHost() );
					AuditManager.insertSession( httpSession, session, level );				
				}				
			} else {
				LOGGER.error( "User {} not found", principal.getShortName() );
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error login audit", th );
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession httpSession = request.getSession(false);

			if ( httpSession != null) {
				Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
				if ( session == null ) {
					insertLoginAudit(httpSession, request );
				} else {
					LOGGER.info( "Session already exists {}", session );
				}
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
	
}
