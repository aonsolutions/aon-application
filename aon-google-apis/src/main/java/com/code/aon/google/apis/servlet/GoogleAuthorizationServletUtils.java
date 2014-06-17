package com.code.aon.google.apis.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.tasks.TasksScopes;


public class GoogleAuthorizationServletUtils {

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT = null;

	private static GoogleClientSecrets CLIENT_SECRETS = null;
	

	public static JsonFactory getJsonFactory() {
		return JSON_FACTORY;
	}

	public static HttpTransport getHttpTransport()
			throws  IOException {
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
				JSON_FACTORY, getClientCredential(),
				Arrays.asList (DriveScopes.DRIVE,DriveScopes.DRIVE_APPDATA, Oauth2Scopes.USERINFO_EMAIL))
					.setAccessType("online")
					.setApprovalPrompt("auto")
					.build();
		

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
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/aon-aio/oauth2callback");

		return url.build();
	}

	public static String getPrincipalShortName(HttpServletRequest req)
			throws ServletException, IOException {
		return  ""; //((AuthPrincipal) req.getUserPrincipal()).getShortName();
	}

}
