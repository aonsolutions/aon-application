package com.code.aon.oauth2.github;

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
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.http.GenericUrl;

public class GithubAuthorizationCodeCallbackServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String CLIENT_ID = "122ab05cea7bbfd793a9";
	static String CLIENT_SECRET = "db120a8e83f92acbbb9533b1bffd6660d7a758d6";
	
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
			
			String redirect = GithubAuthorizationServletUtils.getAuth2CallbackUri(req, authServerName) + "?" + req.getQueryString() ;
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
			github(req,resp,code,state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getAuthToken(HttpServletRequest req, String clientId, String clientSecret,
			String code) throws Exception {

		String body2 = "redirect_uri=" + URLEncoder.encode(GithubAuthorizationServletUtils.getAuth2CallbackUri(req),"UTF-8") + "&" 
				+ "code=" + URLEncoder.encode(code, "UTF-8") + "&" 
				+ "client_id=" + URLEncoder.encode(clientId, "UTF-8") + "&" 
				+ "client_secret=" + URLEncoder.encode(clientSecret, "UTF-8");

		URL url = new URL("https://github.com/login/oauth/access_token");

		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestMethod("POST");
		
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Accept", "application/json");
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

	public static GithubUser getUserProfile(String access_token)
			throws Exception {
		String url = "https://api.github.com/user?access_token=" + access_token;

		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		String responseContent = parseResponse(con.getInputStream());

		JSONObject parsedObject = new JSONObject(responseContent);
		
		String email = parsedObject.getString("email");
		String user_id = parsedObject.getString("id");
		String name = parsedObject.getString("name");
		GithubUser ghu = new GithubUser(user_id, name, email);
		return ghu;
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

	public void github(HttpServletRequest req, HttpServletResponse resp, String code, String state) throws Exception {
		String access_token = getAuthToken(req, CLIENT_ID, CLIENT_SECRET, code);
		GithubUser ghu = getUserProfile(access_token)
				.setState(state)
				.setAcces_token(access_token);
		login(req,resp,ghu);
	}
	
	public void login(HttpServletRequest req, HttpServletResponse resp, GithubUser ghu) throws IOException{
		int pos = req.getParameter("state").indexOf("$");
		String domain = req.getParameter("state").substring(0, pos); //TODO ACTUALIZAR AL TERMINAR AmazonAuthorizationCodeServlet.
		String statepass = "";//req.getParameter("state").substring(pos + 1);
		
		String username = null;
		String email = ghu.getEmail();
		System.out.println("EMAIL = " + email);
		try {
			username = getBDUsername(email, domain);
			if (username != null) {
				SessionUserInfo su = new SessionUserInfo();
				su.setUsername(username);
				if (!su.getGithubUsers().containsKey(email)) {
					su.getGithubUsers().put(email, ghu);
				} else {
					//addTokens to acces to apis!!
				}
				su.setDomain(domain);
				su.setIsGithubSession(true);

				if (!SessionInfo.table.get(domain).getUsers()
						.containsKey(username)) {
					SessionInfo.table.get(domain).getUsers()
							.put(username, su);
				} else {
					SessionInfo.table.get(domain).getUsers()
							.get(username).getGithubUsers().put(email, ghu);
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
		
		Integer port = GithubAuthorizationServletUtils.getServerPort(req);
		GenericUrl url = new GenericUrl(GithubAuthorizationServletUtils.getScheme(req) + "://" + domain
				+ (port != null ? ":" + port : "" )+ req.getContextPath()
				+ "/githubLoginPopupClose/&" + email +
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
