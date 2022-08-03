package com.code.aon.oauth2.fnmt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class FnmtAuthorizationServletUtils {

	/** Global instance of the HTTP transport. */
	private static String PROXY_PORT = "proxyPort";
	private static String PROXY_SCHEME = "proxyScheme";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT = null;


	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}

	public static String getLoginCallbackURL(HttpServletRequest req)
			throws ServletException, IOException {
		return AonStringUtils.substringBeforeLast(req.getRequestURL().toString(), req.getServletPath()) + "/fnmtLoginPopupClose";
	}

	public static String getContextURL(HttpServletRequest req)
			throws ServletException, IOException {
		return AonStringUtils.substringBeforeLast(req.getRequestURL().toString(), req.getServletPath()) ;
	}

}
