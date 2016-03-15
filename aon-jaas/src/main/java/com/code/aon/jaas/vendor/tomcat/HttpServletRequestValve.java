package com.code.aon.jaas.vendor.tomcat;

import java.io.IOException;
import java.security.Principal;
import java.net.IDN;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.ValveBase;

public class HttpServletRequestValve extends ValveBase {

	private static final String SESSION_PROPERTY = "com.code.aon.jaas.session";
	
	/** ThreadLocal to save the HttpServletRequest. */
	private static ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try {
			// Set the ThreadLocal
			setHttpServletRequest(request.getRequest());

			updateSession(request);
			
			// Perform the request
			getNext().invoke(request, response);
			
			serializeGenericPrincipal(request);
		} finally {
			// Unset the ThreadLocal
			setHttpServletRequest(null);
		}
	}
	
	private void updateSession( Request request ) {
        Session session = request.getSessionInternal(false);
        if (session != null) {
        	Principal principal = session.getPrincipal();
            if (principal != null) {
            	if ( session.getSession().getAttribute(SESSION_PROPERTY) == null ) {
                    if (principal instanceof GenericPrincipal) {
                        principal = ((GenericPrincipal) principal).getUserPrincipal();
                    }
            		session.getSession().setAttribute(SESSION_PROPERTY, principal);
            	}
            } else {
            	principal = (Principal) session.getSession().getAttribute(SESSION_PROPERTY);
            	if ( principal != null ) {
            		request.setUserPrincipal(principal);
            	}
            }
        }		
	}
	
	private void serializeGenericPrincipal(Request request) {
        Session session = request.getSessionInternal(false);
        if ( session == null )
        	return;
        Principal principal = request.getPrincipal();
        if ( principal == null )
        	return;
		session.getSession().setAttribute(SESSION_PROPERTY, principal);
	}

	public static HttpServletRequest getHttpServletRequest() {
		return HttpServletRequestValve.httpRequest.get();
	}
	
	public static void setHttpServletRequest( HttpServletRequest request ) {
		httpRequest.set(request);
	}	
	
	public static String getServerName() {
		return IDN.toUnicode(getHttpServletRequest().getServerName());
	}
	
}
