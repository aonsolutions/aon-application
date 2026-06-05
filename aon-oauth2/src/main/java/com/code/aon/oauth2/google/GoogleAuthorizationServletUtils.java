package com.code.aon.oauth2.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.tasks.TasksScopes;

public class GoogleAuthorizationServletUtils {

	/** Global instance of the HTTP transport. */
	private static String PROXY_PORT = "proxyPort";
	private static String PROXY_SCHEME = "proxyScheme";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT = null;

	private static GoogleClientSecrets CLIENT_SECRETS = null;

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

	public static AuthorizationCodeFlow newFlow() throws IOException {
		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
				JSON_FACTORY, getClientCredential(), Arrays.asList(
						DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA,
						Oauth2Scopes.USERINFO_EMAIL, TasksScopes.TASKS,
						GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_COMPOSE,
						GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY))
				.setAccessType("online").setApprovalPrompt("auto").build();

	}

	public static GoogleClientSecrets getClientCredential() throws IOException {
		if (CLIENT_SECRETS == null) {
			CLIENT_SECRETS = GoogleClientSecrets.load(
					JSON_FACTORY,
					new InputStreamReader(GoogleAuthorizationCodeServlet.class
							.getResourceAsStream("/client_secrets.json")));
		}
		return CLIENT_SECRETS;
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
				+ "/oauth2callback");
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
		
		return Integer.decode(port);
	}

}
