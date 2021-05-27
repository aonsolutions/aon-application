package com.code.aon.ui.dbutils.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.master.VersionManager;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;

public class DatabaseUptodate implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUptodate.class.getName());
	
	private String currentVersion;
	
	private String updateCurrentVersion(String domain,VersionManager versionManager) throws AonConnectionException, AonSQLException {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(domain);
			return versionManager.getDatabaseVersion(connection);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}				
		}
	}
	
	public boolean isUpdatable() throws AonConnectionException {
		try {
			VersionManager versionManager = new VersionManager();
			this.currentVersion = updateCurrentVersion(AonUtil.getServerName(),versionManager);
			return versionManager.getAvailableUpdateScripts(currentVersion) != null;
		} catch (AonSQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		}
	}
	public String getCurrentVersion() {
		return currentVersion;
	}
	
}
