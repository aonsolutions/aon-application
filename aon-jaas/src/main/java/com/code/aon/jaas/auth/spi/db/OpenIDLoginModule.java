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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class OpenIDLoginModule extends LoginModule {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(OpenIDLoginModule.class);

	private static final String OPENID_EMAIL = "OpenID_Email=";
	
	private String domain ;

	private Boolean isGoogle = false;
	private Boolean isAmazon = false;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		domain = IDN.toUnicode(HttpServletRequestValve.getHttpServletRequest().getServerName());
	}
	
	@Override
	protected Principal createIdentity(String username) throws Exception {
		
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

			username = getUserName(email, domain);	

			
			if ( username == null ) {
				throw new AuthenticationLoginException( "aon_login_err_6", email);
			}
			
			
			request.getSession().setAttribute("Oauth2callback.email", email);
			request.getSession().setAttribute("isGoogle", isGoogle);
			request.getSession().setAttribute("isAmazon", isAmazon);

		
		}
		
		return super.createIdentity(username);
	}

	@Override
	protected String getUsersPassword() throws LoginException {
		
		String[] info = getUsernameAndPassword();
		if (StringUtils.contains(info[0], OPENID_EMAIL )) {
			try {
				String userAux = StringUtils.substringAfter(info[0],
						OPENID_EMAIL );

				int pos = userAux.indexOf("&");

				String email = userAux.substring(0, pos);
				
				String user = getUserName(email,domain);
				
				
				if ( user==null ) {
					throw new AuthenticationLoginException( "aon_login_err_6", email);
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
				+ " FROM user AS U inner join mail_account AS MA ON (U.id = MA.user_id) inner join domain AS D ON (D.id=U.domain)"
				+ " WHERE MA.email=? AND D.name = ?";

		ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
		
		Util util = new Util(connectionInfo);
		util.createMetadataConnection();
		Domain domain = util.getDomain(domainName);
		connection = connectionInfo.getDomainConnection(domain.getDataBaseName());
	
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
