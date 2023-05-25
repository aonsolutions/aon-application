package com.code.aon.oauth2.amazon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.jaas.auth.spi.db.Domain;
import com.code.aon.jaas.auth.spi.db.Util;
import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class AmazonLoginCallbackServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String pass;
	
	public static String getUsername(String email, String statepass) {
		
		return "OpenID_Email=" + email + "&" + statepass;
	}
	public static String getPassword() {
		return pass;

	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String key = req.getServerName();
		String url = req.getRequestURL().toString();
		Integer pos = url.indexOf("&");
		String email = url.substring(pos+1);
		String username = null;
		//super.doGet(req, resp);
		
		pass = Utils.PasswordGenerator.getPassword(
				Utils.PasswordGenerator.MINUSCULAS
						+ Utils.PasswordGenerator.MAYUSCULAS
						+ Utils.PasswordGenerator.NUMEROS, 10);
		
		/*FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("Oauth2callback.email");
		String email= (String) session;
		*/
		
		
		
		
	
		try {
			username = getBDUsername(email, key);
		} catch (AonConnectionException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		String statepass="";
		if (username !=  null)
			statepass = SessionInfo.table.get(key).getUsers().get(username).getAmazonUsers().get(email).getState();
		
		
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/login/popupclose.jsp");
		req.setAttribute("name", key);
		req.setAttribute("act", SessionInfo.table.get(key).getAction());
		req.setAttribute("username", getUsername(email, statepass));
		req.setAttribute("password", getPassword());

		dispatcher.forward(req, resp);

		
		
		
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
			util.createMetadataConnection(domainName);
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
