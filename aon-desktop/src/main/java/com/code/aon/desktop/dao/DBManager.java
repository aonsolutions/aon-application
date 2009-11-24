package com.code.aon.desktop.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.desktop.DBConnnection;

public class DBManager {

	private static final String DB_SEP = "`";

	private static final Logger LOGGER = Logger.getLogger(DBManager.class.getName());
	
	public static final String AON_MASTER = "aon_master";
	
	private static final String CREATE_SQL = "create.database.3.0.1.sql";
	
	private static final String INSERT_SQL = "default-insert.database.sql";
	
	private URL getCreateSqlURL() {
		return DBManager.class.getResource(CREATE_SQL);
	}

	private URL getInsertSqlURL() {
		return DBManager.class.getResource(INSERT_SQL);
	}
	
	@SuppressWarnings("unchecked")
	private List<String> readSqlScript( URL url, String dbName ) throws IOException {
		InputStream in = url.openStream();
		List<String> lines = IOUtils.readLines( in );
		for( int i = lines.size()-1; i >= 0; i-- ) {
			String line = lines.get(i);
			if ( line.startsWith("#") || StringUtils.isBlank(line) ) {
				lines.remove(i);
			}
		}
		List<String> statements = new LinkedList<String>();
		StringBuffer statement = new StringBuffer();
		for( String line : lines ) {
			int pos = StringUtils.indexOf(line, ";" );
			if ( pos == -1 ) {
				if ( statement.length() > 1 ) {
					statement.append(" ");
				}
				statement.append( StringUtils.trim(line) );
			} else {
				String part1 = StringUtils.substring(line, 0, pos);
				if ( statement.length() > 1 ) {
					statement.append(" ");
				}
				statement.append( StringUtils.trim(part1) );
				String sql = StringUtils.replace( statement.toString(), DB_SEP + AON_MASTER + DB_SEP, DB_SEP + dbName + DB_SEP ); 
				statements.add( sql );
				String part2 = StringUtils.substring(line, pos+1);
				statement = new StringBuffer( StringUtils.trim(part2) );
			}
		}
		return statements;
	}
	
	private Connection getConnection( DBConnnection dbc ) throws SQLException {
		DbUtils.loadDriver(dbc.getDriverClassName());
		String url = StringUtils.substringBeforeLast(dbc.getLabeledURI(), "/") + "/mysql";
	    Connection connection = DriverManager.getConnection(url, dbc.getUid(), dbc.getPassword() );
	    return connection;
	}
	
	private void executeScript( DBConnnection dbc, URL scriptUrl ) throws AonException {
		LOGGER.info( "Executing script " + scriptUrl + " for " + dbc);
		List<String> statements = null;
		try {
			statements = readSqlScript(scriptUrl, dbc.getDBName());
		} catch (IOException e) {
			throw new AonException( "Error reading sql script: " + scriptUrl + ", " + e.getMessage(), e );
		}
	    Connection connection = null;
	    Statement statement = null;
		try {
			connection = getConnection(dbc);
			connection.setAutoCommit(false);
			for( String sql : statements ) {
				statement = connection.createStatement();
				statement.execute(sql);
				statement.close();
			}
		} catch ( SQLException e ) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			DbUtils.closeQuietly(statement);
			statement = null;
			DbUtils.rollbackAndCloseQuietly(connection);
			connection = null;
			throw new AonException( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.commitAndCloseQuietly(connection);
		}
	}
	
	public void dropDB( DBConnnection dbc ) throws SQLException {
	    Connection connection = null;
	    Statement statement = null;
		try {
			connection = getConnection(dbc);
			statement = connection.createStatement();
			String sql = "DROP DATABASE " + DB_SEP + dbc.getDBName() + DB_SEP + "";
			LOGGER.info( "Executing sql: " + sql );
			statement.execute(sql);
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}		
	}

	public boolean exists( DBConnnection dbc ) throws SQLException {
	    Connection connection = null;
	    Statement statement = null;
		try {
			connection = getConnection(dbc);
			statement = connection.createStatement();
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbc.getDBName() + "'";
			LOGGER.info( "Check if exists database: " + sql );
			ResultSet set = statement.executeQuery(sql);
			return set.next();
		} finally {
			DbUtils.closeQuietly(statement);
			DbUtils.closeQuietly(connection);
		}		
	}	
	
	public void createDB( DBConnnection dbc ) throws AonException {
		executeScript( dbc, getCreateSqlURL() );
		executeScript( dbc, getInsertSqlURL() );
	}
	
}
