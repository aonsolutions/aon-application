package com.code.aon.oauth2.amazon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import com.code.aon.jaas.auth.spi.db.Domain;
import com.code.aon.jaas.auth.spi.db.Util;
import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import com.code.aon.oauth2.sessionInfo.SessionUserInfo;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.http.GenericUrl;


public class AmazonAuthorizationCodeCallbackServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String CLIENT_ID = "amzn1.application-oa2-client.b27c3e5b31c14c02b5b11a581eb73610";
	static String CLIENT_SECRET = "10ef6439ec80baebd61cbfcdc0eb63b1776d35207791286b33ef424f6e5045bb";
	
	public static String pass;

	public static String getUsername(String email, String statepass) {
		return "OpenID_Email=" + email + "&" + statepass;
	}

	public static String getPassword() {
		return pass;

	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String reqServerName = req.getServerName();
		String authServerName = req.getParameter("state").substring(0, req.getParameter("state").indexOf("$"));

		if (!reqServerName.equalsIgnoreCase(authServerName)) {
			
			String redirect = AmazonAuthorizationServletUtils.getAuth2CallbackUri(req, authServerName) + "?" + req.getQueryString() ;
			resp.sendRedirect(redirect);
			return;
		}

		HttpServletRequestWrapper redirectReq = new HttpServletRequestWrapper(
				req) {
			@Override
			public StringBuffer getRequestURL() {
				String serverName = req.getServerName();

				return new StringBuffer(
						super.getRequestURL()
								.toString()
								.replaceFirst(serverName.substring(0,
												serverName.indexOf('.')),
										"oauth2callback"));
			}
			
			@Override
			public String getServerName() {
				return "oauth2callback" + req.getServerName().substring(req.getServerName().indexOf('.'));
			}
		};

		super.service(redirectReq, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer buf = req.getRequestURL();
		if (req.getQueryString() != null) {
			buf.append('?').append(req.getQueryString());
		}
		AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(
				buf.toString());
		String code = responseUrl.getCode();
		String state = responseUrl.getState();
		try {
			amazon(req,resp,code,state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getAuthToken(HttpServletRequest req, String clientId, String clientSecret,
			String code) throws Exception {

		String body2 = "grant_type=" + URLEncoder.encode("authorization_code", "UTF-8")+ "&"
				+ "redirect_uri=" + URLEncoder.encode(AmazonAuthorizationServletUtils.getAuth2CallbackUri(req),"UTF-8") + "&" 
				+ "code=" + URLEncoder.encode(code, "UTF-8") + "&" 
				+ "client_id=" + URLEncoder.encode(clientId, "UTF-8") + "&" 
				+ "client_secret=" + URLEncoder.encode(clientSecret, "UTF-8");

		URL url = new URL("https://api.amazon.com/auth/o2/token");

		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Charset", "UTF-8");

		OutputStream os = con.getOutputStream();
		os.write(body2.getBytes("UTF-8"));
		os.flush();

		con.connect();

		String responseContent = parseResponse(con.getInputStream());
		JSONObject parsedObject = new JSONObject(responseContent);
		String accessToken = parsedObject.getString("access_token");
		return accessToken;
	}

	public static AmazonUser getUserProfile(String access_token)
			throws Exception {
		String url = "https://api.amazon.com/user/profile?access_token=" + access_token;

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		String responseContent = parseResponse(con.getInputStream());

		JSONObject parsedObject = new JSONObject(responseContent);
		String email = parsedObject.getString("email");
		String user_id = parsedObject.getString("user_id");
		String name = parsedObject.getString("name");
		AmazonUser au = new AmazonUser(user_id, name, email);
		return au;
	}

	private static String parseResponse(InputStream in) throws Exception {
		InputStreamReader inputStream = new InputStreamReader(in, "UTF-8");
		BufferedReader buff = new BufferedReader(inputStream);

		StringBuilder sb = new StringBuilder();
		String line = buff.readLine();
		while (line != null) {
			sb.append(line);
			line = buff.readLine();
		}

		return sb.toString();
	}

	public void amazon(HttpServletRequest req, HttpServletResponse resp, String code, String state) throws Exception {
		String access_token = getAuthToken(req, CLIENT_ID, CLIENT_SECRET, code);
		AmazonUser au = getUserProfile(access_token);
		au.setState(state);
		login(req,resp,au);
	}
	
	public void login(HttpServletRequest req, HttpServletResponse resp, AmazonUser au) throws IOException{
		int pos = req.getParameter("state").indexOf("$");
		String domain = req.getParameter("state").substring(0, pos); //TODO ACTUALIZAR AL TERMINAR AmazonAuthorizationCodeServlet.
		String statepass = "";//req.getParameter("state").substring(pos + 1);
		
		String username = null;
		String email = au.getEmail();
		System.out.println("EMAIL = " + email);
		//String domain = "novus.aibanez.net"; // coger el dominio del state!!!
		try {
			username = getBDUsername(email, domain);
			if (username != null) {
				SessionUserInfo su = new SessionUserInfo();
				su.setUsername(username);
				if (!su.getAmazonUsers().containsKey(email)) {
					su.getAmazonUsers().put(email, au);
				} else {
					//addTokens to acces to apis!!
				}
				su.setDomain(domain);
				su.setIsAmazonSession(true);

				if (!SessionInfo.table.get(domain).getUsers()
						.containsKey(username)) {
					SessionInfo.table.get(domain).getUsers()
							.put(username, su);
				} else {
					SessionInfo.table.get(domain).getUsers()
							.get(username).getAmazonUsers().put(email, au);
				}
			}

		} catch (AonConnectionException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		pass = Utils.PasswordGenerator.getPassword(
				Utils.PasswordGenerator.MINUSCULAS
						+ Utils.PasswordGenerator.MAYUSCULAS
						+ Utils.PasswordGenerator.NUMEROS, 10);

		/*
		 * RequestDispatcher dispatcher = getServletContext()
		 * .getRequestDispatcher("/login/popupclose.jsp");
		 * req.setAttribute("name", key); req.setAttribute("act",
		 * SessionInfo.table.get(key).getAction()); req.setAttribute("username",
		 * getUsername(email, statepass)); req.setAttribute("password",
		 * getPassword());
		 */
		
		Integer port = AmazonAuthorizationServletUtils.getServerPort(req);
		GenericUrl url = new GenericUrl(AmazonAuthorizationServletUtils.getScheme(req) + "://" + domain
				+ (port != null ? ":" + port : "" )+ req.getContextPath()
				+ "/amazonLoginPopupClose/&" + email +
				"?" + "name=" + domain 
				+ "&act=" + SessionInfo.table.get(domain).getAction()
				+ "&username=" + getUsername(email, statepass)
				+ "&password=" + getPassword().toString()				
		);
		
		resp.sendRedirect(url.build());
	}

	public String getBDUsername(String email, String key)
			throws AonConnectionException, SQLException {
		String username = getUserName(email, key);
		return username;
	}
	
	private String getUserName(String email, String domainName)
			throws AonConnectionException, SQLException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT U.login"
					+ " FROM user AS U inner join mail_account AS MA ON (U.id = MA.user_id) inner join domain AS D ON (D.id=U.domain)"
					+ " WHERE MA.email=? AND D.name = ?";

			ConnectionInfo connectionInfo = ConnectionInfo
					.getDefaultConnectionInfo();

			Util util = new Util(connectionInfo);
			util.createMetadataConnection();
			Domain domain = util.getDomain(domainName);
			connection = connectionInfo.getDomainConnection(domain
					.getDataBaseName());
			/*
			 * DSLContext dslContext= DSL.using(connection,
			 * JooqSettings.getDefaultSettings());
			 * 
			 * Result<Record1<String>> username = dslContext.select(USER.LOGIN)
			 * .from(USER)
			 * .join(MAIL_ACCOUNT).on(USER.ID.eq(MAIL_ACCOUNT.USER_ID))
			 * .join(DOMAIN).on(DOMAIN.ID.eq(USER.DOMAIN))
			 * .where(MAIL_ACCOUNT.EMAIL
			 * .eq(email).and(DOMAIN.NAME.eq(domainName))).fetch();
			 * 
			 * String a= username.format();
			 * 
			 * System.out.println(a);
			 * 
			 * return a;
			 */
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, domainName);
			rs = stmt.executeQuery();

			return rs.next() ? rs.getString("login") : null;

		} finally {

			if (rs != null)
				rs.close();
			if (connection != null)
				connection.close();
			if (stmt != null)
				stmt.close();
		}

	}
}
