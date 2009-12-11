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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.valves.BackDoorAuthenticationValve;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 05/11/2007
 *
 */
public class BackDoorAuthenticationFilter implements Filter {

	/** AuthenticationValve Logger */
	private static final Log LOGGER = LogFactory.getLog( BackDoorAuthenticationFilter.class.getName() );

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
//		LOGGER.fatal( "doFilter in other server" );
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String sso = httpRequest.getParameter( "aonDesktop" );
//		LOGGER.fatal( "doFilter in other server:"  + sso );
		if ( sso != null && Boolean.valueOf( sso ) ) {
			//Get the active request
			String serSessionId = null;
			try {
				serSessionId = getCookie( httpRequest, BackDoorAuthenticationValve.SER_SESSION_ID ).getValue();
			} catch (RuntimeException e) {
				LOGGER.warn( "Accesing directly. Cookie does no exits." + e.getMessage() );
				serSessionId = httpRequest.getSession().getId();
			}
			String requestId = serSessionId + httpRequest.getContextPath();
			Request activeRequest = BackDoorAuthenticationValve.getActiveRequest( requestId );
			if ( activeRequest != null && httpRequest.getAuthType().equals( BackDoorAuthenticationValve.AUTH_TYPE ) ) {
				AuthPrincipal principal = 
					(AuthPrincipal) activeRequest.getNote( BackDoorAuthenticationValve.AUTH_USERNAME_NOTE );
				String password = (String) activeRequest.getNote( BackDoorAuthenticationValve.AUTH_PASSWORD_NOTE );
				if ( principal != null ) {
					String username = principal.getShortName() + IConstants.IDENTITY_SEPARATOR 
										+ principal.getDomain() + activeRequest.getContextPath();
					Principal p = activeRequest.getContext().getRealm().authenticate( username, password ); 
					if( p != null ) {
						register( activeRequest, p, BackDoorAuthenticationValve.AUTH_TYPE );
					} else {
						unregister( activeRequest );
//						//Forward to Login Page.
//						String targetUrl = activeRequest.getContext().getLoginConfig().getLoginPage();
//						RequestDispatcher disp = activeRequest.getRequestDispatcher( targetUrl );
//						disp.forward( activeRequest.getRequest(), activeRequest.getResponse() );
//						activeRequest.getResponse().finishResponse();
//						return;
					}
				}
			}
		}
		chain.doFilter( request, response );
	}

	/**
	 * Gets cookie.
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	private Cookie getCookie(HttpServletRequest request, String name) {
	    boolean found = false;
	    Cookie result = null;
	    Cookie[] cookies = request.getCookies();
	    if (cookies!=null) {
	        int i = 0;
	        while (!found && i < cookies.length) {
	            if (cookies[i].getName().equals(name)) {
	                found=true;
	                result = cookies[i];
	            }
	            i++;
	    	  }
	    }

	    return (result);
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
