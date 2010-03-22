package com.code.aon.ui.audit.session;

import java.io.IOException;
import java.security.Principal;
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

import com.code.aon.audit.Application;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;

public class AuditSessionFilter implements Filter {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditSessionFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	private AuthPrincipal getPrincipal( HttpServletRequest request ) {
		AuthPrincipal user = null;
		Principal principal = request.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} else {
			user = new AuthPrincipal( principal.getName() );
		}
		return user;
	}	
	
	private void insertLoginAudit( HttpSession httpSession, HttpServletRequest request ) {
		AuditManager manager = AuditManager.getInstance();
		try {
			if (! manager.isAuditConfigured() ) {
				manager.configureAudit();
			}			
			AuthPrincipal principal = getPrincipal(request);
			Application application = manager.getApplication(principal);
			User user = manager.getUser( principal.getShortName() );
			if ( application.getAuditLevel() != AuditLevel.NONE ) {
				Session session = new Session();
				session.setApplication( application );
				session.setUser( user );
				session.setSessionId( httpSession.getId() );
				session.setStartDate( new Date(httpSession.getCreationTime()) );
				session.setRemoteAddress( request.getRemoteAddr() );
				session.setRemoteHost( request.getRemoteHost() );
				manager.insertSession( session );
				httpSession.setAttribute( AuditManager.AUDIT_SESSION_PROPERTY, session );				
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
			HttpSession session = request.getSession(false);

			if ( (session != null) && (session.getAttribute(AuditManager.AUDIT_SESSION_PROPERTY) == null) ) {
				insertLoginAudit(session, request );
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
	
}
