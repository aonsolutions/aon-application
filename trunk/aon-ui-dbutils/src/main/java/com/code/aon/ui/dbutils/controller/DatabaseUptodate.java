package com.code.aon.ui.dbutils.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class DatabaseUptodate {

	private String currentVersion;
	private boolean updatable;
	private VersionManager versionManager;
	
	public DatabaseUptodate() {
		versionManager = new VersionManager();		
		setUpdatable(versionManager.getAvailableUpdateScripts(getCurrentVersion()) != null);		
	}
	
	private Connection getConnection() {
		Properties properties = DataSourceUtil.getDBProperties();
		return DataSourceUtil.getConnection(properties);
	}
	
	public String getCurrentVersion() {
		if (currentVersion == null) {
			Connection connection = null;
			try {
				connection = getConnection();
				setCurrentVersion( versionManager.getDatabaseVersion(connection) );
			} catch (AonSQLException e) {
				AonUtil.addErrorMessage("Imposible conseguir el número de versión");
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
}
