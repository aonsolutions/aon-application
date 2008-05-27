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
	private static final String AUTH_PAGE = "/auth/index.jsp";

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get the active request
		Request activeRequest = (Request) BackDoorAuthenticationValve.activeRequest.get();
		String uri = activeRequest.getRequestURI();
		uri = uri.substring( uri.lastIndexOf( "/" ), uri.lastIndexOf( ".auth" ) ) + AUTH_PAGE;
		if ( activeRequest.getQueryString() != null ) {
			uri += "?" + activeRequest.getQueryString();
		}
		String id = activeRequest.getSession( false ).getId();
		activeRequest.recycle();
		activeRequest.getCoyoteRequest().getCookies().recycle();
		activeRequest.getCoyoteRequest().getMimeHeaders().recycle();
		activeRequest.getResponse().recycle();
// TODO Attribute is being remove by Application server.
		activeRequest.getCoyoteRequest().setAttribute( BackDoorAuthenticationValve.SER_SESSION_ID, id );
		Cookie cookie = new Cookie( BackDoorAuthenticationValve.SER_SESSION_ID, id );
		cookie.setPath( "/" );
		activeRequest.getResponse().addCookie( cookie );
		activeRequest.getResponse().sendRedirect( uri );
	}

}
