package com.code.aon.jaas.auth.spi.db;

import java.net.IDN;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class OpenIDLoginModule extends LoginModule {
	private static final String OPENID_EMAIL = "OpenID_Email=";
	private static final String UUID = "uuid";
	
	private String domain ;
	private String token; 
	private Boolean isGoogle = false;
	private Boolean isAmazon = false;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		String t = HttpServletRequestValve.getHttpServletRequest().getParameter("token");
		token = t != null ? IDN.toUnicode(HttpServletRequestValve.getHttpServletRequest().getParameter("token")) : null;
		domain = IDN.toUnicode(HttpServletRequestValve.getHttpServletRequest().getServerName());
	}
	
	@Override
	protected Principal createIdentity(String username) throws Exception {
		if (StringUtils.contains(username, OPENID_EMAIL )) {
			Integer domainId = getDomainId(domain);
			String emailAux = StringUtils.substringAfter(username,
					OPENID_EMAIL );
			int pos = emailAux.indexOf("&");
			
			
			String email = emailAux.substring(0, pos);
			HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
			String pass = (String) request.getSession().getAttribute("Oauth2callback.state");
			
			String emailPass = emailAux.substring(pos+1);
			if(emailPass.contains("$")){
				Integer pos2 = emailPass.indexOf("$");
				emailPass = emailPass.substring(pos2+1);
				isAmazon = true;
			}
			else isGoogle = true;
			
			if(!emailPass.equals(pass)){
				throw new AuthenticationLoginException( "aon_login_err_7", email);

			}

			username = getUserName(domain, domainId, email, null);	

			
			if ( username == null ) {
				throw new AuthenticationLoginException( "aon_login_err_6", email);
			}
			
			
			request.getSession().setAttribute("Oauth2callback.email", email);
			request.getSession().setAttribute("isGoogle", isGoogle);
			request.getSession().setAttribute("isAmazon", isAmazon);

		
		}
		
		else if(token != null) {
			Integer domainId = getDomainId(domain);
			username = getUserName(domain, domainId, null, token);			
		}
		
		return super.createIdentity(username);
	}

	@Override
	protected String getUsersPassword() throws LoginException {
		
		String[] info = getUsernameAndPassword();
		try {
			if (StringUtils.contains(info[0], OPENID_EMAIL )) {
			
				Integer domainId = getDomainId(domain);
				String userAux = StringUtils.substringAfter(info[0],
						OPENID_EMAIL );

				int pos = userAux.indexOf("&");

				String email = userAux.substring(0, pos);
				
				String user = getUserName(domain, domainId, email, null);
				
				
				if ( user==null ) {
					throw new AuthenticationLoginException( "aon_login_err_6", email);
				}
				String password = info[1];
				
				super.getUsersPassword(); // TODO: No comments, only remove it.

				return createPasswordHash(user, password, "storeDigestCallback");
			}
			
			else if(token != null) {
				Integer domainId = getDomainId(domain);
				String user = getUserName(domain, domainId, null, token);
				String password = info[1];
				super.getUsersPassword(); // TODO: No comments, only remove it.
				return createPasswordHash(user, password, "storeDigestCallback");
			}
		} catch (AonConnectionException e) {
			throw new FailedLoginException( e.getLocalizedMessage());
		} catch (SQLException e) {
			throw new FailedLoginException( e.getLocalizedMessage());
		}

		return super.getUsersPassword();
	}

	// -------------------------------------

	private String getUserName(String domainName, Integer domainId, String email, String token) throws AonConnectionException, SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {


			String sql="SELECT U.login"
					+ " FROM user AS U inner join mail_account AS MA ON (U.id = MA.user_id) inner join domain AS D ON (D.id=U.domain OR D.parent = U.domain)"
					+ " WHERE MA.email=? AND D.id = ?";

			if(token != null) {
				sql = "SELECT U.login"
					+ " FROM user AS U inner join domain AS D ON (D.id=U.domain OR D.parent = U.domain)"
					+ " WHERE U.auth=unhex(?) AND D.id = ?";
			} 
			
			connection = AonDataSource.getInstance().getConnection(domainName);
			
			stmt = connection.prepareStatement(sql);

			if(token != null) {
				JSONObject json = decodeJWT(token);
				String uuid = json.getString(UUID);
				stmt.setString(1, uuid);
			} else {
				stmt.setString(1, email);
			}

			stmt.setInt(2, domainId);
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
	
	private Integer getDomainId(String domainName) throws AonConnectionException, SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = AonDataSource.getInstance().getConnection(domainName);
			
			String sql="SELECT D.id"
					+ " FROM domain AS D"
					+ " WHERE D.name = ?";

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, domainName);
			rs = stmt.executeQuery();
			return rs.next() ? rs.getInt("id") : null;
		} finally {

			if (rs != null)
				rs.close();
			if (connection != null)
				connection.close();
			if (stmt != null)
				stmt.close();
		}
	}
	
	public static JSONObject decodeJWT(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return new JSONObject(jwt.getSubject());
	}

}
