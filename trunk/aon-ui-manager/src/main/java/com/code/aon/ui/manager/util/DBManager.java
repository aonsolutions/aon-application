package com.code.aon.ui.manager.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.manager.DBConnnection;
import com.code.aon.master.VersionManager;

public class DBManager {

	private static final String DB_SEP = "`";

	private final static Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
	
	public static final String AON_MASTER = "aon_master";

	private VersionManager versionManager;
	
	public DBManager() {
		this.versionManager = new VersionManager();
	}
	
	private Connection getConnection( DBConnnection dbc ) throws SQLException {
		DbUtils.loadDriver(dbc.getDriverClassName());
		String url = StringUtils.substringBeforeLast(dbc.getLabeledURI(), "/") + "/mysql";
	    Connection connection = DriverManager.getConnection(url, dbc.getUid(), dbc.getUserPasswordString() );
	    return connection;
	}
	
	public void dropDB( DBConnnection dbc ) throws SQLException {
	    Connection connection = null;
	    Statement statement = null;
		try {
			connection = getConnection(dbc);
			statement = connection.createStatement();
			String sql = "DROP DATABASE " + DB_SEP + dbc.getDBName() + DB_SEP + "";
			LOGGER.info( "Executing sql: {}", sql );
			statement.execute(sql);
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}		
	}

	public boolean test( DBConnnection dbc ) throws SQLException {
	    Connection connection = null;
	    boolean connected = false;
		try {
			connection = getConnection(dbc);
			connected = true;
		} finally {
			DbUtils.closeQuietly(connection);
		}	
		return connected;
	}	
	
	public boolean exists( DBConnnection dbc ) {
	    Connection connection = null;
	    Statement statement = null;
		try {
			connection = getConnection(dbc);
			statement = connection.createStatement();
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbc.getDBName() + "'";
			LOGGER.info( "Check if exists database: {}", sql );
			ResultSet set = statement.executeQuery(sql);
			return set.next();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}		
		return false;
	}	
	
	public boolean existsTable( DBConnnection dbc, String tableName ) {
	    Connection connection = null;
	    ResultSet tables = null;
		try {
			connection = getConnection(dbc);
			DatabaseMetaData dbm = connection.getMetaData();
			tables = dbm.getTables(dbc.getDBName(), "", tableName, null);
			return tables.next();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(tables);
			DbUtils.closeQuietly(connection);
		}
		return false;
	}		
	
	public void createDB( DBConnnection dbc ) throws AonException {
	    Connection connection = null;
		try {
			connection = getConnection(dbc);
			String name = dbc.getDBName();
			this.versionManager.createDatabase(connection, name);
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			DbUtils.rollbackAndCloseQuietly(connection);
			connection = null;
			throw new AonException(th.getMessage(), th);
		} finally {
			DbUtils.commitAndCloseQuietly(connection);
		}
	}

	public void insertDefaults( DBConnnection dbc, String application ) throws AonException {
	    Connection connection = null;
		try {
			connection = getConnection(dbc);
			String name = dbc.getDBName();
			this.versionManager.insertApplicationDefaults(connection, name, application);
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			DbUtils.rollbackAndCloseQuietly(connection);
			connection = null;
			throw new AonException(th.getMessage(), th);
		} finally {
			DbUtils.commitAndCloseQuietly(connection);
		}
	}	

}
