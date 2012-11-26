package com.code.aon.ui.dbutils.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;

import com.code.aon.common.AonException;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class DatabaseUptodate {

	private String currentVersion;
	private boolean updatable;
	private boolean connectionAvailable;
	private String connectionErrorMessage;
	private VersionManager versionManager;
	
	public DatabaseUptodate() {
		versionManager = new VersionManager();		
		try {
			setUpdatable(versionManager.getAvailableUpdateScripts(getCurrentVersion()) != null);
			connectionAvailable = true;
		} catch (AonException e) {
			connectionAvailable = false;
			connectionErrorMessage = e.getMessage()
				+ (e.getCause()==null?"": (". " + e.getCause().getMessage()));
			if (StringUtils.contains(connectionErrorMessage,Environment.PASS)) {
				int i = StringUtils.indexOf(connectionErrorMessage, Environment.PASS);
				i = i + Environment.PASS.length();
				int x = StringUtils.indexOf(connectionErrorMessage, ",", i);
				connectionErrorMessage =
						StringUtils.substring(connectionErrorMessage, 0, i+2)
						+ "*********"
						+ StringUtils.substring(connectionErrorMessage, x-1);
						
			}
		}		
	}
	
	private Connection getConnection() throws AonException {
		Properties properties = DataSourceUtil.getDBProperties();
		return ConnectionProvider.getConnection(properties);
	}
	
	public String getCurrentVersion() throws AonException {
		if (currentVersion == null) {
			Connection connection = null;
			try {
				connection = getConnection();
				setCurrentVersion( versionManager.getDatabaseVersion(connection) );
			} catch (AonSQLException e) {
				AonUtil.addErrorMessage("Imposible conseguir el número de versión");
				throw new AonException(e.getMessage(),e);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
					}
				}				
			}
		}
		return currentVersion;
	}
	
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	
	private void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}
	
	public void onUptodate(ActionEvent event) {
		Connection connection = null;
		try {
			connection = getConnection();
			setCurrentVersion( null );
			versionManager.uptodateDatabase(connection);
			setUpdatable(false);
		} catch (AonSQLException e) {
			String msg = "Se produjeron errores al actualizar la base de datos. \n Detalle: " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonException e) {
			String msg = "Se produjeron errores al actualizar la base de datos. \n Detalle: " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}							
		}
	}
	
	public boolean isUptodate() {
		return updatable;
	}

	public boolean isConnectionAvailable() {
		return connectionAvailable;
	}

	public String getConnectionErrorMessage() {
		return connectionErrorMessage;
	}
	
	
}
