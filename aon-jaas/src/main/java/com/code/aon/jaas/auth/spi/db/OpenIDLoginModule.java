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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class OpenIDLoginModule extends LoginModule {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenIDLoginModule.class);

	private static final String OPENID_EMAIL = "OpenID_Email=";
//	private static final String SCHEMA = "schema";
	private static final String SCHEMA_FIRST_DOMAIN = "schema_first_domain";
	private static final String UUID = "uuid";
	
	private String domain ;
	private String token; 
	private Boolean isGoogle = false;
	private Boolean isAmazon = false;
	
	private class AonToken {
		private String email;
		private byte[] auth;

		public String getEmail() {
			return email;
		}

		public AonToken setEmail(String email) {
			this.email = email;
			return this;
		}

		public byte[] getAuth() {
			return auth;
		}

		public AonToken setAuth(byte[] auth) {
			this.auth = auth;
			return this;
		}
	}
	
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
		Integer domainId = getDomainId(domain);
		if (StringUtils.contains(username, OPENID_EMAIL )) {
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
		
//		else if(isEmail(username)) {	
//			byte[] auth = null;
//			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
//			for(String schema: connectionInfo.getSchemas()) {
//				if(auth == null) {
//					auth = getAuth(schema, username);
//				}
//			}	
//			if(auth != null) {
//				username = getUserName(domain, domainId, username, auth);
//			}
//		}
		
		else if(token != null) {
			AonToken aonToken = token(token);
			if(aonToken.getAuth() != null) {
				username = getUserName(domain, domainId, aonToken.getEmail(), aonToken.getAuth());
			}
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
			
//			else if(isEmail(info[0])) {
//				byte[] auth = null;
//				Integer domainId = getDomainId(domain);
//				ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
//				for(String schema: connectionInfo.getSchemas()) {
//					if(auth == null) {
//						auth = getAuth(schema, info[0]);
//					}
//				}	
//				if(auth != null) {
//					String user = getUserName(domain, domainId,  info[0], auth);
//					String password = info[1];
//					super.getUsersPassword(); // TODO: No comments, only remove it.
//					return createPasswordHash(user, password, "storeDigestCallback");
//				} else {
//					throw new AuthenticationLoginException( "aon_login_err_6", info[0]);
//				}
//			}
			
			else if(token != null) {
				AonToken aonToken = token(token);
				Integer domainId = getDomainId(domain);
				if(aonToken.getAuth() != null) {
					String user = getUserName(domain, domainId, aonToken.getEmail(), aonToken.getAuth());
					String password = info[1];
					super.getUsersPassword(); // TODO: No comments, only remove it.
					return createPasswordHash(user, password, "storeDigestCallback");
				} else {
					throw new AuthenticationLoginException( "aon_login_err_6", info[0]);
				}

			}
		} catch (AonConnectionException e) {
			throw new FailedLoginException( e.getLocalizedMessage());
		} catch (SQLException e) {
			throw new FailedLoginException( e.getLocalizedMessage());
		}

		return super.getUsersPassword();
	}

	// -------------------------------------

	private String getUserName(String domainName, Integer domainId, String email, byte[] auth) throws AonConnectionException, SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();

			Util util = new Util(connectionInfo);
			util.createMetadataConnection();
			Domain domain = util.getDomain(domainName);
			connection = connectionInfo.getDomainConnection(domain.getDataBaseName());

			String sql="SELECT U.login"
					+ " FROM user AS U inner join mail_account AS MA ON (U.id = MA.user_id) inner join domain AS D ON (D.id=U.domain)"
					+ " WHERE MA.email=? AND (D.id = ? OR D.parent = ?)";

			if(auth != null) {
				sql = "SELECT U.login"
					+ " FROM user AS U inner join domain AS D ON (D.id=U.domain)"
					+ " WHERE U.auth=? AND (D.id = ? OR D.parent = ?)";
			} 

			stmt = connection.prepareStatement(sql);

			if(auth != null) {
				stmt.setBytes(1, auth);
			} else {
				stmt.setString(1, email);
			}

			stmt.setInt(2, domainId);
			stmt.setInt(3, domainId);
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
			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();

			Util util = new Util(connectionInfo);
			util.createMetadataConnection();
			Domain domain = util.getDomain(domainName);
			connection = connectionInfo.getDomainConnection(domain.getDataBaseName());

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
	
//	private byte[] getAuth(String schema, String email) throws AonConnectionException, SQLException{
//		ResultSet rs = null;
//		Connection connection = null;
//		PreparedStatement stmt = null;
//		try {
//			String sql="SELECT A.id"
//				+ " FROM auth AS A"
//				+ " WHERE A.email= ?";
//
//			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
//
//			Util util = new Util(connectionInfo);
//			util.createMetadataConnection();
//			connection = connectionInfo.getDomainConnection(schema);
//
//			stmt = connection.prepareStatement(sql);
//			stmt.setString(1,email);
//			rs = stmt.executeQuery();
//			return rs.next() ? rs.getBytes("id") : null;
//		} finally {
//
//			if (rs != null)
//				rs.close();
//			if (connection != null)
//				connection.close();
//			if (stmt != null)
//				stmt.close();
//		}
//	}
	
	private AonToken token(String token) throws AonConnectionException, SQLException{
		JSONObject json = decodeJWT(token);
		//String schema = json.getString(SCHEMA);
		String schemaFirstDomain = json.getString(SCHEMA_FIRST_DOMAIN);
		String uuid = json.getString(UUID);

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			String sql="SELECT A.id, A.email"
				+ " FROM auth AS A"
				+ " WHERE A.id = unhex(?)";

			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();

			Util util = new Util(connectionInfo);
			util.createMetadataConnection();
			connection = connectionInfo.getDomainConnection(schemaFirstDomain);

			stmt = connection.prepareStatement(sql);
			stmt.setString(1, uuid);
			rs = stmt.executeQuery();

			return rs.next() ? new AonToken()
					.setEmail(rs.getString("email"))
					.setAuth(rs.getBytes("id")) : new AonToken();
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

//	private boolean isEmail(String email) {
//		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
//                "[a-zA-Z0-9_+&*-]+)*@" + 
//                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
//                "A-Z]{2,7}$";
//		Pattern pat = Pattern.compile(emailRegex); 
//		if (email == null) 
//			return false; 
//		return pat.matcher(email).matches();
//	}

}
