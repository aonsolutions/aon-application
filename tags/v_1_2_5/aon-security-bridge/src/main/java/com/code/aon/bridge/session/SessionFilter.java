/**
 * 
 */
package com.code.aon.bridge.session;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.jmx.mbean.core.JBossConsoleAdminFactory;
import com.code.aon.bridge.jndi.IJNDIConstants;
import com.code.aon.bridge.jndi.SecurityLocator;
import com.code.aon.bridge.jndi.SecurityLocatorException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.session.SessionInfo;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 15/05/2007
 *
 */
public class SessionFilter implements Filter {

    /** Obtiene un logger apropiado. */
	protected static final Log LOGGER = LogFactory.getLog( SessionFilter.class.getName() );

	/*
     * Does nothing.
	 */
	public void destroy() {	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession httpSession = httpRequest.getSession(false);
		try {
			IConsoleAdmin console = ConsoleAdminFactoryManager.createConsoleAdmin();
			String oname = console.getAonSessionManagerName();
			if ( httpRequest.getUserPrincipal() != null ) {
				Object[] params = { httpSession.getId() };
				String[] sig = {String.class.getName()};
				Object obj = console.invoke( oname, IOperation.GET_SESSION_INFO, params, sig );
				if ( obj == null ) {
					Principal principal = httpRequest.getUserPrincipal(); 
					AuthPrincipal auth = 
						( principal instanceof AuthPrincipal )? (AuthPrincipal) principal: new AuthPrincipal( httpRequest.getUserPrincipal().getName() ); 
					SessionInfo sessionInfo = 
						new SessionInfo( httpSession.getId()
								, httpSession.getCreationTime()
								, httpSession.getLastAccessedTime(), httpSession.getMaxInactiveInterval()
								, auth ); 
					console.invoke( oname, IOperation.REGISTER_SESSION, new Object[] {sessionInfo}, new String[] {SessionInfo.class.getName()} );
				}
			} else {
				if ( httpSession != null ) {
					Integer jsf_sequence = (Integer) httpSession.getAttribute( "jsf_sequence" );
					if ( jsf_sequence != null && jsf_sequence > 1 ) {
						AuthenticationLoginException failed = 
							(AuthenticationLoginException) console.invoke( oname, IOperation.GET_LASTLOGIN_EXCEPTION, new Object[] {IConsoleAdmin.EMPTY_STRING}, new String[] {String.class.getName()} );
						httpRequest.setAttribute( FailedLogin.AON_LAST_EXCEPTION_KEY, failed );
					}
				}
			}
		} catch (DeploymentException e) {
			throw new ServletException(e);
		}
		chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		Object[] params = {true};
        String[] sig = {Boolean.class.getName()};
		IConsoleAdmin console = null;
		try {
			try {
				console = SecurityLocator.getInstance().getConsole(IJNDIConstants.CONSOLE_FACTORY_CLASS);
			} catch (SecurityLocatorException e) {
				JBossConsoleAdminFactory FACTORY = new JBossConsoleAdminFactory();
				if ( FACTORY.accept() )
					console = FACTORY.createConsoleAdmin();
	    	}
			String oname = console.getAonSessionManagerName();
			console.invoke( oname, IOperation.ENABLE_CONCURRENT_SESSIONS, params, sig );
		} catch (DeploymentException e) {
			throw new ServletException(e);
		}
	}

}
