package com.code.aon.oauth2.fnmt;

import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public class FnmtAuthorizationServletUtils {

	private static final String X_FORWARDED_PROTO  = "X-Forwarded-Proto";
	
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}

	public static String getLoginCallbackURL(HttpServletRequest req)
			{
		String  loginCallbackURL = AonStringUtils.substringBeforeLast(req.getRequestURL().toString(), req.getServletPath()) + "/fnmtLoginPopupClose";
		loginCallbackURL = AonStringUtils.replace(loginCallbackURL, req.getScheme(), getXForwardedScheme(req));
		return loginCallbackURL;
	}

	public static String getContextURL(HttpServletRequest req)
			{
		String contextURL =  AonStringUtils.substringBeforeLast(req.getRequestURL().toString(), req.getServletPath()) ;
		contextURL = AonStringUtils.replace(contextURL, req.getScheme(), getXForwardedScheme(req));
		return contextURL;
	}
	
	private static String getXForwardedScheme(HttpServletRequest req) {
		return AonStringUtils.defaultIfBlank(req.getHeader(X_FORWARDED_PROTO), req.getScheme());
	}

}
