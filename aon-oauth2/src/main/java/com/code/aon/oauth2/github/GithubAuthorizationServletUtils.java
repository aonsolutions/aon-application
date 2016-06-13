package com.code.aon.oauth2.github;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GithubAuthorizationServletUtils {

	/** Global instance of the HTTP transport. */
	private static String PROXY_PORT = "proxyPort";
	private static String PROXY_SCHEME = "proxyScheme";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT = null;
	
	private static String CLIENT_ID = "122ab05cea7bbfd793a9";
	private static String CLIENT_SECRET = "db120a8e83f92acbbb9533b1bffd6660d7a758d6";

	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}

	public static HttpTransport getHttpTransport() throws IOException {
		if (HTTP_TRANSPORT == null) {
			try {
				HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			} catch (GeneralSecurityException e) {
				throw new IOException(e);
			}
		}
		return HTTP_TRANSPORT;
	}


	public static String getClientId() throws IOException {
		return CLIENT_ID;
	}
	
	public static String getClientCredential() throws IOException {
		return CLIENT_SECRET;
	}

	public static String getAuth2CallbackUri(HttpServletRequest req)
			throws ServletException, IOException {
		String servername = req.getServerName();
		return getAuth2CallbackUri(req, "oauth2callback" + servername.substring(servername.indexOf('.')));
	}

	public static String getAuth2CallbackUri(HttpServletRequest req,
			String domain) throws ServletException, IOException {
		Integer port = getServerPort(req);
		GenericUrl url = new GenericUrl(getScheme(req) + "://" + domain
				+ (port != null ? ":" + port : "" )+ req.getContextPath()
				+ "/githuboauth2callback");
		return url.build();
	}

	public static String getPrincipalShortName(HttpServletRequest req)
			throws ServletException, IOException {
		return ""; // ((AuthPrincipal) req.getUserPrincipal()).getShortName();
	}

	public static String getScheme(HttpServletRequest req) {
		return System.getProperty(PROXY_SCHEME, req.getScheme());
	}
	
	public static Integer getServerPort(HttpServletRequest req) {
		String scheme = getScheme(req);
		String port = System.getProperty(PROXY_PORT, String.valueOf(req.getServerPort()));
		if ( scheme.equals("http") && port.equals("80") ) 
			return null;
		if ( scheme.equals("https") && port.equals("443")) 
			return  null;
		
		return null;//Integer.decode(port);
	}

}
