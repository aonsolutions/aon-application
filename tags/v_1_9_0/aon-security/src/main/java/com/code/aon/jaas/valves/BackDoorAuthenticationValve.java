/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IRole;

/**
 * This class validates a user forward using an encrypted file from one context to another
 * in Tomcat servlet container.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 22/10/2007
 */
public abstract class BackDoorAuthenticationValve extends ValveBase implements IConstants {

	/** Authentication SSO name. */
	public static final String BACKDOOR_PARAM = "aonDesktop";

	/** BackDoorAuthenticationValve Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(BackDoorAuthenticationValve.class);

	/** Maintain the application server Principals for programmatic web login */
	protected Map<String, BackDoorPrincipal> backdoorPrincipals = new HashMap<String, BackDoorPrincipal>();
    /** Has this component been deployed? */
	protected boolean deployed = false;

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		Session session = request.getSessionInternal( false );
		if ( !deployed )
			storeDeployed();

		if ( session != null ) {
			String nonHashedPassword = (String) session.getNote( Constants.SESS_PASSWORD_NOTE );
			if ( nonHashedPassword != null ) {
				String username = (String) session.getNote( Constants.SESS_USERNAME_NOTE );
				LOGGER.debug( "Adding principal: {} session: {}", username, session.getId() );
				backdoorPrincipals.put( session.getId(), new BackDoorPrincipal( username, nonHashedPassword ) );
				request.setAttribute( AUTH_PASSWORD_NOTE, nonHashedPassword );
			}
		}

		if ( request.getRequestURI().indexOf( "auth" ) > -1 ) {
			forward( request, session.getId() );
			return;
		}

		boolean isApplicationInitialRequest = 
			request.getRequestURI().equals( request.getContextPath() + "/" );
		if ( isApplicationInitialRequest && isValidRequest( request ) && request.getParameter( BACKDOOR_PARAM ) != null ) {
			session = request.getSessionInternal( true );
			BackDoorPrincipal bdp = null;
			if ( backdoorPrincipals.containsKey( session.getId() ) ) {
				bdp = backdoorPrincipals.get( session.getId() );
			} else { // Remote access
				String serSessionId = null;
				try {
					serSessionId = getCookie( request.getRequest(), SER_SESSION_ID ).getValue();
					bdp = (BackDoorPrincipal) Util.getSSOPrincipal( mserver, getAonSessionManager(), serSessionId );
				} catch (RuntimeException e) {
					LOGGER.warn( "Asking for REQUEST parameter. Cookie does no exits. " + serSessionId, e);
				} catch (Exception e) {
					LOGGER.error( e.getMessage(), e );
				}
			}
			LOGGER.debug( "Using Authenticated Principal: {} SIZE: {}", bdp, backdoorPrincipals.size() );
			if ( bdp != null ) {
				String username = bdp.getPrincipal().getShortName() + IConstants.IDENTITY_SEPARATOR 
								+ bdp.getPrincipal().getDomain() + request.getContextPath();
				List<String> roles = getRoles( request.getContextPath() );
				LOGGER.debug( "Registering: {}, with the following roles: {}", username, roles );
				register( request, new AonGenericPrincipal( request, username, bdp.getPassword(), roles ), AUTH_TYPE );
			}
		}
		try {
			getNext().invoke( request, response );// Perform the request
		} finally {
			request.removeNote( AUTH_TYPE );
			request.removeNote( AUTH_USERNAME_NOTE );
			request.removeNote( AUTH_PASSWORD_NOTE );
			request.removeAttribute( AUTH_PASSWORD_NOTE );
			if ( request.getParameter( BACKDOOR_PARAM ) != null )
				try {
					Util.removeSSOPrincipal( mserver, getAonSessionManager(), session.getId() );
				} catch (Exception e) {
					LOGGER.error( "Error removing SSO principal for this session: " + session.getId(), e );
				}
		}
	}

	/**
	 * Return <code>MainDeployer</code> object name.
	 * 
	 * @return
	 * @throws MalformedObjectNameException
	 */
	protected abstract ObjectName getMainDeployer() throws MalformedObjectNameException;
	/**
	 * Return <code>AonSessionManager</code> object name.
	 * 
	 * @return
	 * @throws MalformedObjectNameException
	 */
	protected abstract ObjectName getAonSessionManager() throws MalformedObjectNameException;
	/**
	 * Return <code>AonLdap</code> object name.
	 * 
	 * @return
	 * @throws MalformedObjectNameException
	 */
	protected abstract ObjectName getAonLdap() throws MalformedObjectNameException;
	/**
	 * Flush <code>BackDoorPrincipal</code> in the AonSessionManager MBean using 
	 * the <blockquote>IP</blockquote> passed by parameter to allocate the application server.  
	 * 
	 * @param IP
	 * @param sessionId
	 * @throws NamingException
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 * @throws IOException
	 */
	protected abstract void flushRemoteAccess(String IP, String sessionId) 
				throws NamingException, MalformedObjectNameException, NullPointerException
				, InstanceNotFoundException, MBeanException, ReflectionException, IOException;
	/**
	 * Store deployed applications name in a file.
	 * 
	 * @throws IOException
	 */
	protected abstract void storeDeployed() throws IOException;

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

	private boolean isValidRequest(Request request) {
		boolean authRequired = true;
        // Is this request URI subject to a security constraint?
        SecurityConstraint[] constraints = 
        	request.getContext().getRealm().findSecurityConstraints( request, request.getContext() );
        if ( constraints == null ) {
        	return false;
        } else {
        	// Since authenticate modifies the response on failure, we have to check for allow-from-all first.
	        for(int i=0; i < constraints.length && authRequired; i++) {
	            if(!constraints[i].getAuthConstraint()) {
	                authRequired = false;
	            } else if(!constraints[i].getAllRoles()) {
	                String [] roles = constraints[i].findAuthRoles();
	                if(roles == null || roles.length == 0) {
	                    authRequired = false;
	                }
	            }
	        }
        }
		return authRequired;
	}

	/**
	 * Register the principal with the request, session etc just the way AuthenticatorBase does.
	 * 
	 * @param request Catalina Request
	 * @param principal <code>AonGenericPrincipal</code> generated via authentication
     * @param authType The authentication type to be registered
	 */
	private void register(Request request, AonGenericPrincipal principal, String authType) {
		request.setAuthType( authType );
		request.setUserPrincipal( principal ); 
		if ( principal != null ) {
			request.setNote( AUTH_USERNAME_NOTE, principal.getUserPrincipal() );
			request.setNote( AUTH_PASSWORD_NOTE, principal.getCredentials() );
		}
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setAuthType( authType );
			session.setPrincipal( principal );
		}
		flushSessionPrincipal( session.getId(), principal );
	}

	/**
	 * Forward request to a remote application server.
	 * 
	 * @param request
	 * @param sessionId
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forward(Request request, String sessionId) throws ServletException, IOException {
		String uri = request.getRequestURI();
		uri = uri.substring( uri.lastIndexOf( "/" ), uri.lastIndexOf( ".auth" ) );
		String ip = Util.findStoredApplicationIp( InetAddress.getLocalHost().getHostAddress(), uri );
		if ( request.getQueryString() != null ) {
			uri += "?" + request.getQueryString();
		}
		try {
			flushRemoteAccess( ip, sessionId );
			Cookie cookie = new Cookie( SER_SESSION_ID, sessionId );
			cookie.setPath( "/" );
			request.getResponse().addCookie( cookie );
			request.getResponse().sendRedirect( uri );
		} catch (Exception e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/**
	 * Flush principal in the AonSessionManager MBean.
	 * 
	 * @param sessionId
	 * @param principal
	 */
	private void flushSessionPrincipal(String sessionId, AonGenericPrincipal principal) {
		try {
			LOGGER.debug( "Flushing LOCAL session:" + sessionId + " principal:" + principal.getUserPrincipal() );
			Object[] params = { sessionId, principal };
			String[] sig = { String.class.getName(), Object.class.getName() };
			mserver.invoke( getAonSessionManager(), "flushSSOPrincipal", params, sig );
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

	/**
	 * Return the list of application roles.
	 * 
	 * @param ctx
	 * @return
	 */
	private List<String> getRoles(String ctx) {
		try {
			List<String> roles = new ArrayList<String>();
			Object[] params = { ctx };
			String[] sig = { String.class.getName() };
			IApplication app = 
				(IApplication) mserver.invoke( getAonLdap(), "getApplication4Ctx", params, sig );
			Iterator<IRole> iter = app.roles().iterator(); 
			while ( iter.hasNext() ) {
				IRole role = iter.next();
				roles.add( role.getId() );
			}
			return roles;
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (NullPointerException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (InstanceNotFoundException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MBeanException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (ReflectionException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}

}