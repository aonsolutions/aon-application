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

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class FnmtLoginModule extends OpenIDLoginModule {
	private static final String FNMT_DNI = "Fnmt_Dni=";
	
	private String domain ;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		domain = IDN.toUnicode(HttpServletRequestValve.getDomainName());
	}
	
	@Override
	protected Principal createIdentity(String username) throws Exception {
		if (StringUtils.contains(username, FNMT_DNI )) {
			String dni = StringUtils.substringAfter(username, FNMT_DNI );
			username = getUserName(domain, dni );	
			if ( username == null ) {
				throw new AuthenticationLoginException( "aon_login_err_6", dni);
			}
		} 
		return super.createIdentity(username);
	}

	@Override
	protected String getUsersPassword() throws LoginException {
		String[] info = getUsernameAndPassword();
		try {
			if (StringUtils.contains(info[0], FNMT_DNI )) {
				String dni = StringUtils.substringAfter(info[0], FNMT_DNI );
				String user = getUserName(domain, dni);
				if ( user==null ) {
					throw new AuthenticationLoginException( "aon_login_err_6", dni);
				}
				String password = info[1];
				super.getUsersPassword(); // TODO: No comments, only remove it.
				return createPasswordHash(user, password, "storeDigestCallback");
			} 
		} catch (AonConnectionException | SQLException e) {
			throw new FailedLoginException( e.getLocalizedMessage());
		} 

		return super.getUsersPassword();
	}

	
	// -------------------------------------

	private String getUserName(String domainName, String dni) throws AonConnectionException, SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
			String auth = getAuth(domainName, dni);

			String sql = "SELECT U.login"
					+ " FROM user AS U inner join domain AS D ON (D.id=U.domain OR D.parent = U.domain)"
					+ " WHERE U.auth=unhex(?) AND D.name = ?"
					+ " UNION "
					+ "SELECT U.login"
					+ " FROM user AS U inner join registry AS R ON (U.registry = R.id) inner join domain AS D ON (D.id=U.domain OR D.parent = U.domain)"
					+ " WHERE R.document=? AND D.name = ?";

			
			connection = AonDataSource.getInstance().getConnection(domainName);
			
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, auth);
			stmt.setString(2, domainName);

			stmt.setString(3, dni);
			stmt.setString(4, domainName);
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
	
	private String getAuth(String domainName, String dni) throws AonConnectionException, SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = AonDataSource.getInstance().getConnection(domainName);
			stmt = connection.prepareStatement("SELECT hex(A.id) AS uuid FROM auth AS A WHERE A.document=?");
			stmt.setString(1, dni);
			rs = stmt.executeQuery();
			
			return rs.next() ? rs.getString("uuid") : null;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	

}
