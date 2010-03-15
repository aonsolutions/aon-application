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

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.session.SessionInfo;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 15/05/2007
 */
public class SessionFilter implements Filter {

	@Override
	public void destroy() {	}

	@Override
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Object[] params = {true};
        String[] sig = {Boolean.class.getName()};
		try {
			IConsoleAdmin console = Utils.getSecurityConsole();
			String oname = console.getAonSessionManagerName();
			console.invoke( oname, IOperation.ENABLE_CONCURRENT_SESSIONS, params, sig );
		} catch (DeploymentException e) {
			throw new ServletException(e);
		}
	}

}
