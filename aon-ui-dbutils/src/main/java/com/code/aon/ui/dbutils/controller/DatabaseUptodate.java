package com.code.aon.ui.dbutils.controller;

import java.sql.Connection;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;
import com.code.aon.ui.util.AonUtil;

public class DatabaseUptodate {

	private String currentVersion;
	private boolean updatable;
	private VersionManager versionManager;
	
	public DatabaseUptodate() {
		versionManager = new VersionManager();		
		setUpdatable(versionManager.getAvailableUpdateScripts(getCurrentVersion()) != null);		
	}
	
	public String getCurrentVersion() {
		if (currentVersion == null) {
			Connection c = null;
			try {
				String sessionFactoryName = HibernateUtil.getSessionFactoryName(); 
				c = HibernateUtil.getSQLConnection(sessionFactoryName);
				setCurrentVersion( versionManager.getDatabaseVersion(c) );
			} catch (AonSQLException e) {
				AonUtil.addErrorMessage("Imposible conseguir el número de versión");
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
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(); 
			Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
			versionManager.uptodateDatabase(c);
			setCurrentVersion( null );
			setUpdatable(false);
		} catch (AonSQLException e) {
			String msg = "Se produjeron errores al actualizar la base de datos. \n Detalle: " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public boolean isUptodate() {
		return updatable;
	}
}
