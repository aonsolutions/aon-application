/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;


/**
 * This class validates a user forward using an encrypted file from one context to another
 * in Tomcat servlet container.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 22/10/2007
 */
public class BackDoorAuthenticationValve extends ValveBase {

	/** Serialized session identifier name. */
	public static final String SER_SESSION_ID= "serSessionId";

	/** Maintain the Principals for programmatic web login */
	private static final Map<String, BackDoorPrincipal> sessions = new HashMap<String, BackDoorPrincipal>();
	/** Maintain the Catalina active Requests for programmatic web login */
	private static final Map<Integer, ThreadLocal<Request>> activeRequests = new HashMap<Integer, ThreadLocal<Request>>();

	/* (non-Javadoc)
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		Session session = request.getSessionInternal( false );
		if ( session != null ) {
			String nonHashedPassword = (String) session.getNote( Constants.SESS_PASSWORD_NOTE );
			if ( nonHashedPassword != null ) {
				String username = (String) session.getNote( Constants.SESS_USERNAME_NOTE );
				sessions.put( session.getId(), new BackDoorPrincipal( username, nonHashedPassword, request.hashCode() ) );
			}
		}
		if ( !activeRequests.containsKey( request.hashCode() ) ) {
			ThreadLocal<Request> activeRequest = new ThreadLocal<Request>();
			activeRequest.set( request ); // Set the active request
			activeRequests.put( request.hashCode(), activeRequest );
		} else {
			activeRequests.get( request.hashCode() ).set( request );
		}
		try {
			getNext().invoke( request, response );// Perform the request
		} finally {
			activeRequests.get( request.hashCode() ).set( null );
		}
	}

	/**
	 * Return the active Request.
	 * 
	 * @param serSessionId
	 * @return
	 */
	public static final synchronized Request getActiveRequest(String serSessionId) {
		int hashCode = sessions.get( serSessionId ).getRequestHashcode();
		return activeRequests.get( hashCode ).get();
	}

	/**
	 * Return <code>BackDoorPrincipal</code> bound to sessionId.
	 * 
	 * @param serSessionId
	 * @return
	 */
	public static final synchronized BackDoorPrincipal getPrincipal(String serSessionId) {
		return sessions.get( serSessionId );
	}

}
