package com.code.aon.google.apis.servlet;


import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;



public class GoogleAuthorizationCodeServlet extends
		AbstractAuthorizationCodeServlet {

	@Override
	protected String getRedirectUri(HttpServletRequest req)
			throws ServletException, IOException {
		return getAuth2CallbackUri(req);
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException,
			IOException {
		return getPrincipalShortName(req);
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		return newFlow();		
	}
}
