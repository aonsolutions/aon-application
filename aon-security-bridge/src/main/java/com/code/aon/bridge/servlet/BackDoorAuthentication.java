/**
 * 
 */
package com.code.aon.bridge.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;

import com.code.aon.jaas.valves.BackDoorAuthenticationValve;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 31/10/2007
 *
 */
public class BackDoorAuthentication extends HttpServlet {

	private static final long serialVersionUID = -1236276509878590335L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get the active request
		String requestId = request.getSession().getId() + request.getContextPath();
		Request activeRequest = BackDoorAuthenticationValve.getActiveRequest( requestId );
		String uri = activeRequest.getRequestURI();
		uri = uri.substring( uri.lastIndexOf( "/" ), uri.lastIndexOf( ".auth" ) );
		if ( activeRequest.getQueryString() != null ) {
			uri = "http://aon.inetserver.net:8080" + uri + "?" + activeRequest.getQueryString();
		}
////		String id = activeRequest.getSession( false ).getId();
//		activeRequest.recycle();
//		activeRequest.getCoyoteRequest().getCookies().recycle();
//		activeRequest.getCoyoteRequest().getMimeHeaders().recycle();
//		activeRequest.getResponse().recycle();
// TODO Attribute is being remove by Application server.
//		activeRequest.getCoyoteRequest().setAttribute( BackDoorAuthenticationValve.SER_SESSION_ID, id );
		Cookie cookie = new Cookie( BackDoorAuthenticationValve.SER_SESSION_ID, requestId );
		cookie.setPath( "/" );
		System.out.println( activeRequest.getNote( BackDoorAuthenticationValve.AUTH_USERNAME_NOTE ) + " " + activeRequest.getNote( BackDoorAuthenticationValve.AUTH_PASSWORD_NOTE ));
		System.out.println( activeRequest.getUserPrincipal() + " " + activeRequest.getPrincipal() );
		activeRequest.getResponse().addHeader( "AUTH_USER", "iayerbe@inetserver.net/aon-account" );
		activeRequest.getResponse().addHeader( "AUTH_PASSWORD", "demo" );
		activeRequest.getResponse().addCookie( cookie );
		activeRequest.getResponse().sendRedirect( uri );
	}

}
