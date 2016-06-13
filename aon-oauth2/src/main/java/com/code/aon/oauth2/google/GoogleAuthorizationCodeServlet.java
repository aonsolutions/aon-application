package com.code.aon.oauth2.google;

import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getAuth2CallbackUri;
import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getScheme;
import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.getServerPort;
import static com.code.aon.oauth2.google.GoogleAuthorizationServletUtils.newFlow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionEnterpriseInfo;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;



public class GoogleAuthorizationCodeServlet extends
		AbstractAuthorizationCodeServlet {
	
	private static final long serialVersionUID = 1L;

	
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
		Integer port = getServerPort(req);
		String action= getScheme(req)+"://"+key+ ( port != null ? ":" + port: "" )+req.getContextPath()+"/j_security_check"; 
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
		req.getSession().setAttribute("Oauth2callback.state", pass);
		// TODO Apéndice de método generado automáticamente
		super.onAuthorization(req, resp, authorizationUrl);
	}
	
}


