package com.code.aon.jaas.auth;

import java.util.List;

import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;

public class AonGenericPrincipal extends GenericPrincipal {

	/** The active request */
	public ThreadLocal<Request> request = new ThreadLocal<Request>();
	/** The authenticated user credentials */
	private Object credentials = null;

	public AonGenericPrincipal(Request request, String username, Object credentials, List<String> roles) {
		super( request.getContext().getRealm(), username, null, roles, new AuthPrincipal(username) );
		this.request.set( request );
		this.credentials = credentials;
	}

	public Object getCredentials() {
		return credentials;
	}

	public Request getRequest() {
		return request.get();
	}

}
