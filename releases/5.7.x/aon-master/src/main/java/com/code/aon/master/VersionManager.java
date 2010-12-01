package com.code.aon.master;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.AonSQLFile;
import com.code.aon.dbutils.AonSQLScript;

public class VersionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonSQLScript.class.getName());
	
	public String[] getVersions() {
		return IConstants.VERSIONS;
	}
	
	public String getLastVersion() {
		return IConstants.VERSIONS[IConstants.VERSIONS.length-1];
	}

	public URL getCreateScript() {
		String name = getCreateScriptName();
		return getScript(name);
	}

	public URL getUpdateScript(String version) {
		String name = getUpdateScriptName(version);
		return getScript(name);
	}
	
	public URL getInsertScript(String application) {
		String name = getInsertScriptName(application);
		return getScript(name);
	}

	public URL getScript(String name) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return cl.getResource(name);
	}

	public URL[] getAvailableUpdateScripts(String initialVersion) {
		if (initialVersion == null) {
			String msg = "A initial version must be provided!.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}

		// Se busca el índice de la versión pasada por param.
		int idx = 0;
		for (; idx < IConstants.VERSIONS.length && !initialVersion.equals(IConstants.VERSIONS[idx]) ; idx++);
		
		if (idx < (IConstants.VERSIONS.length - 1)) { // La última versión no tiene update file.
			String[] versions = Arrays.copyOfRange(IConstants.VERSIONS, idx, (IConstants.VERSIONS.length - 1));
			URL[] urls = new URL[versions.length];
			for (int i = 0; i < versions.length; i++) {
				urls[i] = getUpdateScript(versions[i]);
			}
			return urls;
		}
		return null;
	}

	private String getCreateScriptName() {
		return IConstants.CREATE_SCRIPT_PREFIX + IConstants.SCRIPT_SUFFIX;
	}

	private String getUpdateScriptName(String version) {
		return IConstants.UPDATE_SCRIPT_PREFIX + version + IConstants.SCRIPT_SUFFIX;
	}
	
	private String getInsertScriptName(String application) {
		return IConstants.INSERT_SCRIPT_PREFIX + application + IConstants.SCRIPT_SUFFIX;
	}	

	public String getDatabaseVersion(Connection c) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = c.prepareStatement(IConstants.DATABASE_VERSION_STMT);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			String msg = "Table 'db_version' has no data!.";
			LOGGER.error(msg);
			throw new AonSQLException(msg);
		} catch (SQLException e) {
			throw new AonSQLException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private void execute( Connection c, URL url, String dbName ) throws IOException, AonSQLException {
		LOGGER.info("sql script: {}", url.getFile());
		AonSQLFile file = new AonSQLFile(url.openStream());
		if ( dbName != null ) {
			file.setDbName(dbName);
		}
		file.setFileName( url.getFile());
		AonSQLScript script = new AonSQLScript(file, c);
		script.execute();
		LOGGER.info("script done !" );
	}	

	public void uptodateDatabase(Connection c) throws AonSQLException {
		try {
			String currentVersion = getDatabaseVersion(c);
			URL[] urls = getAvailableUpdateScripts(currentVersion);
			if (! ArrayUtils.isEmpty(urls) ) {
				for (URL url : urls) {
					execute(c, url, null);
				}
			}
			LOGGER.info("Database is uptodate!");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			throw new AonSQLException(e.getMessage(),e); 
		}
	}

	public void createDatabase(Connection c, String dbName) throws AonSQLException {
		try {
			URL createUrl = getCreateScript();
			if (createUrl != null) {
				execute(c, createUrl, dbName);
			}
			URL insertUrl = getInsertScript(IConstants.INSERT_DEFAULT_SCRIPT);
			if (insertUrl != null) {
				execute(c, insertUrl, dbName);
			}
			LOGGER.info("Database is created!");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			throw new AonSQLException(e.getMessage(),e); 
		}
	}
	
	public void insertApplicationDefaults(Connection c, String dbName, String application) throws AonSQLException {
		try {
			URL insertUrl = getInsertScript(application);
			if (insertUrl != null) {
				execute(c, insertUrl, dbName);
			}
			LOGGER.info("Application {} defaults inserted!", application);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			throw new AonSQLException(e.getMessage(),e); 
		}
	}	

}
