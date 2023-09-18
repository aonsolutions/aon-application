package com.code.aon.oauth2.github;


import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionEnterpriseInfo;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;

public class GithubAuthorizationCodeServlet extends HttpServlet {

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
	  protected void service(HttpServletRequest req, HttpServletResponse resp)
	      throws IOException, ServletException {
	    
	      String authorizationUrl = "https://github.com/login/oauth/authorize"
	      		+ "?client_id="+getClientId()
	      		+ "&scope=user,repo"
	      		//+ "&response_type=code"
	      		+ "&redirect_uri=" + getRedirectUri(req);
	    
	      onAuthorization(req, resp, authorizationUrl);	
	  }

	  /**
	   * Loads the authorization code flow to be used across all HTTP servlet requests (only called
	   * during the first HTTP servlet request).
	   */
	  protected  AuthorizationCodeFlow initializeFlow() throws ServletException, IOException{
		  return null;
	  }

	  /** Returns the redirect URI for the given HTTP servlet request. */
	  protected  String getRedirectUri(HttpServletRequest req)
	      throws ServletException, IOException{
		  
		  String key = req.getServerName();
			Integer port = GithubAuthorizationServletUtils.getServerPort(req);
			String action= GithubAuthorizationServletUtils.getScheme(req)+"://"+key+ ( port != null ? ":" + port: "" )+req.getContextPath()+"/j_security_check"; 
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
		  
		  return GithubAuthorizationServletUtils.getAuth2CallbackUri(req);
	  }

	  /** Returns the user ID for the given HTTP servlet request. */
	  protected  String getUserId(HttpServletRequest req) throws ServletException, IOException{
		  return GithubAuthorizationServletUtils.getPrincipalShortName(req);
	  }

	  protected  String getClientId() throws ServletException, IOException{
		  return GithubAuthorizationServletUtils.getClientId();
	  }


	  protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp,
	      String authorizationUrl) throws ServletException, IOException {
			String pass = Utils.PasswordGenerator.getPassword(
					Utils.PasswordGenerator.MINUSCULAS
					+ Utils.PasswordGenerator.MAYUSCULAS
					+ Utils.PasswordGenerator.NUMEROS, 10);
			
			String redirectUrl = authorizationUrl + "&state="+ req.getServerName()+"$"+pass;
			req.getSession().setAttribute("Oauth2callback.state", pass);
			resp.sendRedirect(redirectUrl);
	  }
	 


}
