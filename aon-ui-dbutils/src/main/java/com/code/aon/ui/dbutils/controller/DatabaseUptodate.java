package com.code.aon.ui.dbutils.controller;

import static com.code.aon.ui.dbutils.controller.IDbutilsConstants.BUNDLE_NAME;
import static com.code.aon.ui.dbutils.controller.IDbutilsConstants.BUNDLE_RESOURCE;
import static com.code.aon.ui.dbutils.controller.IDbutilsConstants.DOMAIN_NOT_EXIST;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

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

	private Properties properties;
	private String currentVersion;
	private boolean updatable;
	private boolean connectionAvailable;
	private String connectionErrorMessage;
	private VersionManager versionManager;
	
	private boolean init;
	
	public DatabaseUptodate() {
		versionManager = new VersionManager();		
	}
	
	public String getCurrentVersion() throws AonException {
		if (currentVersion == null) {
			Connection connection = null;
			try {
				connection = ConnectionProvider.getConnection(properties);
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
			connection = ConnectionProvider.getConnection(properties);
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
		if (! this.init ) {
			init(DataSourceUtil.getDBProperties(), AonUtil.getServerName(), AonUtil.getCurrentLocale());
		}
		return updatable;
	}

	public boolean isConnectionAvailable() {
		return connectionAvailable;
	}

	public String getConnectionErrorMessage() {
		return connectionErrorMessage;
	}

	public void init( Properties dbProperties, String server, Locale locale ) {
		try {
			this.properties = dbProperties;
			if ( this.properties != null ) {
				setUpdatable(versionManager.getAvailableUpdateScripts(getCurrentVersion()) != null);
				connectionAvailable = true;				
			} else {
				ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_RESOURCE, locale);
	    		MessageFormat mf = new MessageFormat( bundle.getString(DOMAIN_NOT_EXIST) ); 
				connectionErrorMessage =  mf.format( new Object[]{server} );
			}
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
		} finally {
			this.init = true;
		}
	}
	
}
