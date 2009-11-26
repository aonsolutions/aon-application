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

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.valves.BackDoorAuthenticationValve;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 05/11/2007
 *
 */
public class BackDoorAuthenticationFilter implements Filter, IConstants {

	/** AuthenticationValve Logger */
	private final static Logger LOGGER = LoggerFactory.getLogger(BackDoorAuthenticationFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String sso = httpRequest.getParameter( BackDoorAuthenticationValve.BACKDOOR_PARAM );
		if ( sso != null && Boolean.valueOf( sso ) ) {
			AonGenericPrincipal agp = null;
			try {
				agp = Utils.getSSOPrincipal( httpRequest.getSession().getId() );
			} catch (DeploymentException e) {
				LOGGER.error( e.getMessage(), e );
			}
			if ( agp != null && httpRequest.getAuthType().equals( AUTH_TYPE ) ) {
				AuthPrincipal principal = (AuthPrincipal) agp.getUserPrincipal();
				if ( principal != null ) {
					String username = getUserName(principal, httpRequest);
					Principal p = agp.getRealm().authenticate( username, (String) agp.getCredentials() ); 
					if( p != null ) {
						register( agp.getRequest(), p, AUTH_TYPE );
					} else {
						unregister( agp.getRequest() );
					}
				}
			}
		}
		chain.doFilter( request, response );
	}
	
	private String getUserName( AuthPrincipal principal, HttpServletRequest httpRequest) {
		String domain = DomainResolver.getDomain(httpRequest);
		String username = principal.getShortName() + IConstants.IDENTITY_SEPARATOR 
			+ domain + httpRequest.getContextPath();
		return username;
	}

	/**
	 * Register the principal with the request, session etc just the way AuthenticatorBase does.
	 * 
	 * @param request Catalina Request
	 * @param principal User Principal generated via authentication
     * @param authType The authentication type to be registered
	 */
	private void register(Request request, Principal principal, String authType) {
		request.setAuthType( authType );
		request.setUserPrincipal( principal ); 
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setAuthType( authType );
			session.setPrincipal( principal );
		}
	}

	/**
	 * UnRegister the principal with the request and session.
	 * 
	 * @param request Catalina Request
	 */
	private void unregister(Request request) {
		request.setUserPrincipal( null ); 
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setPrincipal( null );
		}
	}
}
