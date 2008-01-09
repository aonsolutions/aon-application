/**
 * 
 */
package com.code.aon.bridge.session;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.valves.BackDoorAuthenticationValve;
import com.code.aon.jaas.valves.BackDoorPrincipal;
import com.code.aon.jaas.valves.DesEncrypter;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 05/11/2007
 *
 */
public class BackDoorAuthenticationFilter implements Filter {

	/** AuthenticationValve Logger */
	private static final Log LOGGER = LogFactory.getLog( BackDoorAuthenticationFilter.class.getName() );
	/** Authentication methods for login configuration. */
	private static final String AUTH_TYPE = "PROGRAMMATIC_WEB_LOGIN";

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
		//Get the active request
		Request activeRequest = (Request) BackDoorAuthenticationValve.activeRequest.get();
		String serSessionId = 
			(String) activeRequest.getCoyoteRequest().getAttribute( BackDoorAuthenticationValve.SER_SESSION_ID );
		if ( serSessionId == null )
			try {
				serSessionId = getCookie( httpRequest, BackDoorAuthenticationValve.SER_SESSION_ID ).getValue();
			} catch (RuntimeException e) {
				LOGGER.warn( "Accesing directly. Cookie does no exits." + e.getMessage() );
			}
		if ( httpRequest.getUserPrincipal() == null && serSessionId != null ) {
			BackDoorPrincipal bdp = deserialize( serSessionId );
			if ( bdp != null ) {
				String username = 
					bdp.getPrincipal().getShortName() + "@" + bdp.getPrincipal().getDomain() 
					+ activeRequest.getContextPath();
				Principal principal = 
					activeRequest.getContext().getRealm().authenticate( username, bdp.getPassword() ); 
				if( principal != null ) {
					register( activeRequest, principal, username, bdp.getPassword() );
					RequestDispatcher disp = activeRequest.getRequestDispatcher( "/" );
					disp.forward( activeRequest.getRequest(), activeRequest.getResponse() );
				} else {
					//Forward to Login Page.
					String targetUrl = activeRequest.getContext().getLoginConfig().getLoginPage();
					RequestDispatcher disp = activeRequest.getRequestDispatcher( targetUrl );
					disp.forward( activeRequest.getRequest(), activeRequest.getResponse() );
				}
				activeRequest.getResponse().finishResponse();
				return;
			}
		}
		chain.doFilter( request, response );
	}

	protected BackDoorPrincipal deserialize(String serSessionId) {
		FileInputStream istream = null;
		try {
			SecretKey key = DesEncrypter.getSecretKeyInstance( BackDoorAuthenticationValve.SER_EXT + serSessionId );
			String path = 
				BackDoorAuthenticationValve.RESOURCES_DEFAULT_DIR + serSessionId + BackDoorAuthenticationValve.SER_EXT;
			istream = new FileInputStream( path );
			/* Create the output stream */
			ObjectInputStream p = new ObjectInputStream( istream );
			SealedObject so = (SealedObject) p.readObject();
			return (BackDoorPrincipal) so.getObject( key );
		} catch(IOException e) {
			LOGGER.fatal( e.getMessage() );
		} catch (ClassNotFoundException e) {
			LOGGER.fatal( e.getMessage() );
		} catch (InvalidKeySpecException e) {
			LOGGER.fatal( e.getMessage() );
		} catch (NoSuchAlgorithmException e) {
			LOGGER.fatal( e.getMessage() );
		} catch (InvalidKeyException e) {
			LOGGER.fatal( e.getMessage() );
		} finally {
			if ( istream != null )
				try {
					istream.close();
				} catch(IOException e) {
				}
		}
		return null;
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
	 * @param username username passed by the user (null for client-cert)
	 * @param credential Password (null for client-cert and digest)
	 */
	private void register(Request request, Principal principal, String username, Object password) {
		request.setAuthType(AUTH_TYPE);
		request.setUserPrincipal(principal); 
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( true );
		if(session != null) {
			session.setAuthType(AUTH_TYPE);
			session.setPrincipal(principal);
			if (username != null)
				session.setNote(Constants.SESS_USERNAME_NOTE, username);
			else
				session.removeNote(Constants.SESS_USERNAME_NOTE);
			if (password != null)
				session.setNote(Constants.SESS_PASSWORD_NOTE, getPasswordAsString(password));
			else
				session.removeNote(Constants.SESS_PASSWORD_NOTE);
		}
	}

	/**
	 * Returns credential as String.
	 * 
	 * @param cred
	 * @return
	 */
	private String getPasswordAsString(Object cred) {
		String p = null;
		if(cred instanceof String) {
			p = (String)cred;
		} else if(cred instanceof byte[]) {
			p = new String((byte[])cred); 
		}
		return p;
	}

}
