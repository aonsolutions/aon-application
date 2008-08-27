package com.code.aon.ui.audit.session;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.code.aon.audit.Application;
import com.code.aon.audit.Domain;
import com.code.aon.audit.LoginAudit;
import com.code.aon.audit.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.audit.AuditManager;

public class AuditSessionFilter implements Filter {
	
	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AuditSessionFilter.class.getName());	

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
	
	public String getApplicationName( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		int pos = application.lastIndexOf(".");
		if ( pos != -1 ) {
			application = application.substring(0, pos);
		}
		return application;
	}	
	
	private void insertLoginAudit( HttpSession session, AuthPrincipal principal ) {
		AuditManager manager = AuditManager.getInstance();
		manager.changeToAuditDB();
		try {
			String applicationName = getApplicationName(principal.getContext() );
			Application application = manager.getApplication(applicationName);
			Domain domain = manager.getDomain(principal.getDomain());
			User user = manager.getUser( principal.getShortName(), domain );
			Date date = new Date( session.getCreationTime() );
			LoginAudit audit = manager.createLoginAudit(application, user, session.getId(), date );
			session.setAttribute( AuditSessionListener.LOGIN_AUDIT_PROPERTY, audit );
		} catch ( Throwable th ) {
			LOGGER.log( Level.SEVERE, "Error login audit", th );
		} finally {
			manager.restoreToPreviousDB();	
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		if ( (servletRequest != null) && (servletRequest instanceof HttpServletRequest) ) {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpSession session = request.getSession(false);

			if ( session != null ) {
				insertLoginAudit(session, getPrincipal(request) );
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
	
}
