package com.esferalia.aon.gwt.common.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.pool.AonConnectionException;

public class AonServletUtils {

	public static Connection getConnection() throws SQLException {
		try {
			HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
			AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
			String domainName = authPrincipal.getDomain();
			Connection connection = DatabaseUtil.getConnection(domainName);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(),e);
		}
	}

	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
		}
	}

	public static void commit(Connection conn) throws SQLException {
		conn.commit();
	}


	public static void execute(Connection connection, String... sqls)
			throws SQLException {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			for (String sql : sqls) {
				stmt.execute(sql);
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public static void disableAutoCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
	}

	public static void enableAutoCommit(Connection conn) {
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
		}
	}
	
}
