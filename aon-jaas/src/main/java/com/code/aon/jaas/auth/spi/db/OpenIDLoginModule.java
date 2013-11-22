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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;

public class OpenIDLoginModule extends LoginModule {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenIDLoginModule.class);

	private static final String OPENID_EMAIL = "OpenID_Email=";
	
	private String domain ;

	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		domain = IDN.toUnicode(HttpServletRequestValve.getHttpServletRequest().getServerName());
	}
	
	@Override
	protected Principal createIdentity(String username) throws Exception {
		String name = username;
		if (StringUtils.contains(name, OPENID_EMAIL )) {
			String email = StringUtils.substringAfter(name,
					OPENID_EMAIL );
			name = getUserName(email, domain);	
			if ( name == null ) {
				throw new AuthenticationLoginException( "aon_login_err_6", email);
			}
		}
		

		
		return super.createIdentity(name);
	}

	@Override
	protected String getUsersPassword() throws LoginException {
		
		String[] info = getUsernameAndPassword();
		if (StringUtils.contains(info[0], OPENID_EMAIL )) {
			try {
				String user = getUserName(StringUtils.substringAfter(info[0],
						OPENID_EMAIL ),domain);

				if ( user == null ) {
					throw new AuthenticationLoginException( "aon_login_err_6", StringUtils.substringAfter(info[0],
							OPENID_EMAIL ));
				}
				String password = info[1];
				
				super.getUsersPassword(); // TODO: No comments, only remove it.

				return createPasswordHash(user, password, "storeDigestCallback");

			} catch (AonConnectionException e) {
				throw new FailedLoginException( e.getLocalizedMessage());
			} catch (SQLException e) {
				throw new FailedLoginException( e.getLocalizedMessage());
			}
		}
		
		return super.getUsersPassword();
	}

	// -------------------------------------

	private String getUserName(String email, String domainName) throws AonConnectionException, SQLException{
		
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
		String sql="SELECT U.login"
				+ " FROM user AS U inner join mail_account AS MA ON U.id = MA.user_id"
				+ " WHERE MA.email=?";

		ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
		
		Util util = new Util(connectionInfo);
		util.createMetadataConnection();
		Domain domain = util.getDomain(domainName);
		connection = connectionInfo.getDomainConnection(domain.getDataBaseName());
		
		stmt = connection.prepareStatement(sql);
		stmt.setString(1,email);
		rs = stmt.executeQuery();
		
		return rs.next() ? rs.getString("login") : null;
		
		}finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}
}
