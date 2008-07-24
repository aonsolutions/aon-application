/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.valves.ValveBase;


/**
 * This class validates a user forward using an encrypted file from one context to another
 * in Tomcat servlet container.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 22/10/2007
 */
public class BackDoorAuthenticationValve extends ValveBase {

	public static final String AUTH_USERNAME_NOTE = "com.code.aon.jaas.valves.USERNAME";
	public static final String AUTH_PASSWORD_NOTE = "com.code.aon.jaas.valves.PASSWORD";
	/** Authentication methods for login configuration. */
	public static final String AUTH_TYPE = "com.code.aon.jaas.valves.PROGRAMMATIC_WEB_LOGIN";
	/** Serialized session identifier name. */
	public static final String SER_SESSION_ID = "serSessionId";
	/** Authentication SSO name. */
	public static final String AUTH_SSO = "desktop";

	/** Maintain the Catalina active Requests for programmatic web login */
	private static final Map<String, ThreadLocal<Request>> activeRequests = new HashMap<String, ThreadLocal<Request>>();

	/** Maintain the application server Principals for programmatic web login */
	private Map<String, BackDoorPrincipal> backdoorPrincipals = new HashMap<String, BackDoorPrincipal>();
	/** Maintain the application server Principals for programmatic web login */
	private Map<String, Object> principals = new HashMap<String, Object>();

	/* (non-Javadoc)
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		Session session = request.getSessionInternal( false );
//System.out.println( "VALVE invoke: " + session + " " + request.getRequestURI() );
		if ( session != null ) {
			String nonHashedPassword = (String) session.getNote( Constants.SESS_PASSWORD_NOTE );
//System.out.println( "PRINCIPALS VALVE invoke: " + session.getNote( Constants.SESS_USERNAME_NOTE ) + " " + nonHashedPassword );
			if ( nonHashedPassword != null ) {
				String username = (String) session.getNote( Constants.SESS_USERNAME_NOTE );
				BackDoorPrincipal bdp = new BackDoorPrincipal( username, nonHashedPassword );
				backdoorPrincipals.put( session.getId(), bdp );
				request.setNote( AUTH_PASSWORD_NOTE, bdp.getPassword() );
			}
		}

		if ( isValidRequest( request ) ) {
			//	Only set security elements if reauthentication is not required
			if ( request.getContextPath().indexOf( AUTH_SSO ) == -1 ) {
				session = request.getSessionInternal( true );
//System.out.println( "VALVE invoke: " + session.getId() + " " + request.getRequestURI() + " " + request.getNote( AUTH_PASSWORD_NOTE ) + "##" + principals.size());
				if ( session != null && principals.containsKey( session.getId() ) ) {
					Principal principal = (Principal) principals.get( request.getRequestedSessionId() );
					BackDoorPrincipal bdp = backdoorPrincipals.get( session.getId() );
					register( request, principal, bdp, AUTH_TYPE );
				}
			}
			if ( session != null ) {
				String requestId = session.getId() + request.getContextPath();
				// Only set active request in case of requested URI is at a different JBoss.
				if ( !activeRequests.containsKey( requestId ) ) {
					ThreadLocal<Request> activeRequest = new ThreadLocal<Request>();
					activeRequest.set( request ); // Set the active request
//System.out.println( "PUT activeRequests: " + requestId + " " + activeRequest.get().getRequestURI() );
					activeRequests.put( requestId, activeRequest );
				} else {
//System.out.println( "UPDATE activeRequests:" + requestId);
					activeRequests.get( requestId ).set( request );
				}
			}
//System.out.println( "REQUEST SIZE: "  + activeRequests.size() );
		}

		try {
			getNext().invoke( request, response );// Perform the request
            if ( request.getContextPath().indexOf( AUTH_SSO ) > -1 
            		&& request.getRequestURI().endsWith( Constants.FORM_ACTION ) )
				associate( session );
		} finally {
			request.removeNote( AUTH_TYPE );
			request.removeNote( AUTH_USERNAME_NOTE );
			request.removeNote( AUTH_PASSWORD_NOTE );
			if ( session != null ) {
				String requestId = session.getId() + request.getContextPath();
				ThreadLocal<Request> activeRequest = activeRequests.get( requestId );
				if ( activeRequest != null ) {
					activeRequest.set( null );
				}
			}
		}
	}

	/**
	 * Return the active Request.
	 * 
	 * @param requestId
	 * @return
	 */
	public static final synchronized Request getActiveRequest(String requestId) {
		return activeRequests.get( requestId ).get();
	}

	private boolean isValidRequest(Request request) {		
    	boolean authRequired = true;
        Context context = request.getContext();
        // Is this request URI subject to a security constraint?
        SecurityConstraint[] constraints = context.getRealm().findSecurityConstraints( request, context );
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

	private void associate(Session session) {
		if( session != null )
			principals.put( session.getId(), session.getNote( Constants.FORM_PRINCIPAL_NOTE ) );
	}

	/**
	 * Register the principal with the request, session etc just the way AuthenticatorBase does.
	 * 
	 * @param request Catalina Request
	 * @param principal User Principal generated via authentication
     * @param bdp Username and Password used to authenticate (if any)
     * @param authType The authentication type to be registered
	 */
	private void register(Request request, Principal principal, BackDoorPrincipal bdp, String authType) {
		request.setAuthType( authType );
		request.setUserPrincipal( principal ); 
		if ( bdp != null ) {
			request.setNote( AUTH_USERNAME_NOTE, bdp.getPrincipal() );
			request.setNote( AUTH_PASSWORD_NOTE, bdp.getPassword() );
		}
		//Cache the authentication principal in the session
		Session session = request.getSessionInternal( false );
		if(session != null) {
			session.setAuthType( authType );
			session.setPrincipal( principal );
		}
	}

}