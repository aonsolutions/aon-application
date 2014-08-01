package com.code.aon.google.apis.servlet;

import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getAuth2CallbackUri;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getHttpTransport;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getJsonFactory;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.getPrincipalShortName;
import static com.code.aon.google.apis.servlet.GoogleAuthorizationServletUtils.newFlow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.sessionInfo.GoogleUser;
import com.code.aon.google.apis.sessionInfo.SessionInfo;
import com.code.aon.google.apis.sessionInfo.SessionUserInfo;
import com.code.aon.jaas.auth.spi.db.Domain;
import com.code.aon.jaas.auth.spi.db.Util;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.services.drive.Drive;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.tasks.Tasks;


public class GoogleAuthorizationCodeCallbackServlet extends
		AbstractAuthorizationCodeCallbackServlet {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static String pass;
	

	
	public static String getUsername(String email,String statepass) {
		return "OpenID_Email=" + email+"&"+statepass;
	}

	public static String getPassword() {
		return 	pass;

	}
	public String getBDUsername(String email,String key) throws AonConnectionException, SQLException{
		String username = getUserName(email, key);
		return username;
	}
	
	
	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp,
			Credential credential) throws ServletException, IOException {
		
		super.onSuccess(req, resp, credential);

		
		String username = null;
		int pos=req.getParameter("state").indexOf("&");
		String key = req.getParameter("state").substring(0, pos);
		String statepass= req.getParameter("state").substring(pos+1); 
		
		Oauth2 oauth2 = new Oauth2.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
		
		Drive drive = new Drive.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
		
		
		System.out.println(drive);
		Tasks tasks=new Tasks.Builder(getHttpTransport(), getJsonFactory(), credential)
				.setApplicationName("AON SOLUTIONS").build();
				
		
		String email = oauth2.userinfo().v2().me().get().execute().getEmail();
		System.out.println("EMAIL = " + email );
		
		try {
			username = getBDUsername(email,key);
			
			if ( username != null ) {
			
				GoogleUser gu = new GoogleUser();
				gu.setDrive(drive);
				gu.setGmail(email);
				gu.setOAuth2(oauth2);
				gu.setTasks(tasks);
				
				SessionUserInfo su = new SessionUserInfo();
			
				
				su.setUsername(username);
				//su.setGoogleUsers(new Hashtable<String, GoogleUser>());
				
				if (!su.getGoogleUsers().containsKey(email)){
					su.getGoogleUsers().put(email, gu);
				}
				else{
					su.getGoogleUsers().get(email).setDrive(drive);
					su.getGoogleUsers().get(email).setOAuth2(oauth2);
					su.getGoogleUsers().get(email).setTasks(tasks);
				}
				
				su.setDomain(key);
				su.setIsGoogleSession(true);
			
				if(!SessionInfo.table.get(key).getUsers().containsKey(username)){
					SessionInfo.table.get(key).getUsers().put(username, su);
				}
				else{
					SessionInfo.table.get(key).getUsers().get(username).getGoogleUsers().put(email, gu);
				}
			}

			
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		pass = Utils.PasswordGenerator.getPassword(
				Utils.PasswordGenerator.MINUSCULAS
				+ Utils.PasswordGenerator.MAYUSCULAS
				+ Utils.PasswordGenerator.NUMEROS, 10);
		
		credential.getClientAuthentication().toString();
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/login/popupclose.jsp");
		req.setAttribute("name", key);
		req.setAttribute("act",SessionInfo.table.get(key).getAction()); 
		req.setAttribute("username", getUsername(email,statepass));
		req.setAttribute("password", getPassword());
		dispatcher.forward(req, resp);
		
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
	
	
	
	private String getUserName(String email, String domainName) throws AonConnectionException, SQLException{
		
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
			
		
		String sql="SELECT U.login"
				+ " FROM user AS U inner join mail_account AS MA ON (U.id = MA.user_id) inner join domain AS D ON (D.id=U.domain)"
				+ " WHERE MA.email=? AND D.name = ?";

		ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
		
		Util util = new Util(connectionInfo);
		util.createMetadataConnection();
		Domain domain = util.getDomain(domainName);
		connection = connectionInfo.getDomainConnection(domain.getDataBaseName());
		/*
		DSLContext dslContext= DSL.using(connection, JooqSettings.getDefaultSettings());
		
		Result<Record1<String>> username = dslContext.select(USER.LOGIN)
			.from(USER)
			.join(MAIL_ACCOUNT).on(USER.ID.eq(MAIL_ACCOUNT.USER_ID))
			.join(DOMAIN).on(DOMAIN.ID.eq(USER.DOMAIN))
			.where(MAIL_ACCOUNT.EMAIL.eq(email).and(DOMAIN.NAME.eq(domainName))).fetch();
		
		String a= username.format();
		
		System.out.println(a);
		
		return a;
		*/
		stmt = connection.prepareStatement(sql);
		stmt.setString(1,email);
		stmt.setString(2, domainName);
		rs = stmt.executeQuery();
		
		return rs.next() ? rs.getString("login") : null;
		
		}finally {
		
			if (rs != null)
				rs.close();
			if (connection != null)
				connection.close();
			if (stmt != null)
				stmt.close();
		}
		
	}
	
}
