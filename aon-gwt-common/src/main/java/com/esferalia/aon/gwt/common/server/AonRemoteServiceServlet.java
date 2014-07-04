package com.esferalia.aon.gwt.common.server;

import javax.servlet.http.HttpServletRequest;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class AonRemoteServiceServlet extends RemoteServiceServlet {

	AuthPrincipal getAuthPrincipal() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
		return authPrincipal;
	}
	
	Integer getDomainID() {
		return getAuthPrincipal().getDomainId();
	}
	
}
