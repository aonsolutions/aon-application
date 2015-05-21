package com.code.aon.gps.servlet.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ServletUtils implements ISQLConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(ServletUtils.class.getName());
	public static String SELECT_LOGIN =
			"SELECT 1 FROM raddinfo" +
			" WHERE domain = ?" +
			" AND attribute = ?" +
			" AND value = ?";

	public boolean isValidLogin(Connection connection, Integer domainId, String login) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(SELECT_LOGIN, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			SQLUtils.setInt(stmt, 1, domainId);
			SQLUtils.setString(stmt, 2, SERVLET_LOGIN);
			SQLUtils.setString(stmt, 3, login);
			rs = stmt.executeQuery();
			return rs.next();
		} catch (Throwable ex) {
			LOGGER.error(ex.getMessage(), ex);
			return false;
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

}
