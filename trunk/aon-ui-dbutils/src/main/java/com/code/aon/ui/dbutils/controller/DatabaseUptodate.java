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
	
	private VersionManager getVersionManager() {
		if (versionManager == null) {
			versionManager = new VersionManager();
		}
		return versionManager; 
	}
	public String getCurrentVersion() {
		if (currentVersion == null) {
			try {
				String sessionFactoryName = HibernateUtil.getSessionFactoryName(); 
				Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
				setCurrentVersion( getVersionManager().getDatabaseVersion(c) );
			} catch (AonSQLException e) {
				AonUtil.addErrorMessage("Imposible conseguir el número de versión");
			}
		}
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	public boolean isUpdatable() {
		return updatable;
	}
	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}
	
	public void onCheck(ActionEvent event) {
		setCurrentVersion( null );
		setUpdatable(getVersionManager().getAvailableUpdateScripts(getCurrentVersion()) != null);
	}
	public void onUptodate(ActionEvent event) {
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(); 
			Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
			getVersionManager().uptodateDatabase(c);
			setCurrentVersion( null );
			setUpdatable(false);
		} catch (AonSQLException e) {
			String msg = "Se produjeron errores al actualizar la base de datos. \n Detalle: " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public boolean isUptodate() {
		onCheck(null);
		return updatable;
	}
}
