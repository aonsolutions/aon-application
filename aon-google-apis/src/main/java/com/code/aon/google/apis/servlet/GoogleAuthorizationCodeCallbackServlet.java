package com.code.aon.google.apis.servlet;

import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getAuth2CallbackUri;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getHttpTransport;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getJsonFactory;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.newFlow;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.google.apis.DriveUtils;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.services.drive.Drive;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.Tasks.TasksOperations;

public class GoogleAuthorizationCodeCallbackServlet extends
		AbstractAuthorizationCodeCallbackServlet {
	
	public static String email;
	public static String pass;
	
	public static Drive drive;
	public static Tasks tasks;
	public static Oauth2 oauth2;
	
	public static String getUsername() {
		return "OpenID_Email=" + 
	GoogleAuthorizationCodeCallbackServlet.email;
	}

	public static String getPassword() {
		return 	pass;

	}

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
			Credential credential) throws ServletException, IOException {
		
		super.onSuccess(req, resp, credential);
		
		//CalendarUtils.initialize(req);
		//CalendarUtils.addCalendaar(new Calendar());
		
		
		oauth2 = new Oauth2.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
		
		drive = new Drive.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
		req.getSession().setAttribute("Drive", drive);
		
		System.out.println(drive);
		tasks=new Tasks.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
		req.getSession().setAttribute("Tasks", tasks);
		
		email = oauth2.userinfo().v2().me().get().execute().getEmail();
		
		System.out.println("EMAIL = " + email );
		
		pass = PasswordGenerator.getPassword(
				PasswordGenerator.MINUSCULAS
				+ PasswordGenerator.MAYUSCULAS
				+ PasswordGenerator.ESPECIALES, 10);
				
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/login/popupclose.jsp");
		req.setAttribute("username", getUsername());
		req.setAttribute("password", getPassword());
		dispatcher.forward(req, resp);
		
		
		//DriveUtils.initialize();
		
		//DriveUtils.get();
		
		/*TaskUtils.initialize(req);
		try {
			TaskUtils.synchronize();
		} catch (SQLException e) {
			// TODO Bloque catch generado autom·ticamente
			e.printStackTrace();
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado autom·ticamente
			e.printStackTrace();
		}*/
		
	}
	
	
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
	
	public static class PasswordGenerator {

		public static final String NUMEROS = "0123456789";

		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

		public static final String ESPECIALES = "Ò—";

		//
		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}

		public static String getPassword() {
			return getPassword(8);
		}

		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}

		public static String getPassword(String key, int length) {
			String pswd = "";

			for (int i = 0; i < length; i++) {
				pswd += key.charAt((int) (Math.random() * key.length()));
			}

			return pswd;
		}
	}
	
}
