package com.code.aon.google.apis.servlet;


import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.sessionInfo.SessionEnterpriseInfo;
import com.code.aon.google.apis.sessionInfo.SessionInfo;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;



public class GoogleAuthorizationCodeServlet extends
		AbstractAuthorizationCodeServlet {

	
	public static String name;
	public static String act;
	
	public static String getName(){
		return name;
	}
	
	public static String getAct(){
		return act;
	}
	
	
	@Override
	protected String getRedirectUri(HttpServletRequest req)
			throws ServletException, IOException {
		String key = req.getServerName();
		String action= req.getScheme()+"://"+key+":"+req.getServerPort()+req.getContextPath()+"/j_security_check"; 
		name=key;
		act=action;
		if (!SessionInfo.table.containsKey(key)){
			SessionEnterpriseInfo se = new SessionEnterpriseInfo();
			se.setAction(action);
			se.setDomain(key);
			SessionInfo.table.put(key, se);
		}
		else {
			SessionInfo.table.get(key).setDomain(key);
			SessionInfo.table.get(key).setAction(action);
		}

		
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

	@Override
	protected void onAuthorization(HttpServletRequest req,
			HttpServletResponse resp,
			AuthorizationCodeRequestUrl authorizationUrl)
			throws ServletException, IOException {
		
		String pass = Utils.PasswordGenerator.getPassword(
				Utils.PasswordGenerator.MINUSCULAS
				+ Utils.PasswordGenerator.MAYUSCULAS
				+ Utils.PasswordGenerator.NUMEROS, 10);
		
		authorizationUrl.setState(req.getServerName()+"&"+pass);
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		request.getSession().setAttribute("Oauth2callback.state", pass);
		// TODO Apéndice de método generado automáticamente
		super.onAuthorization(req, resp, authorizationUrl);
	}
	
}


