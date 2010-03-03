package com.code.aon.desktop.dao;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.desktop.DBConnnection;

public class DBManager {

	private static final String DB_SEP = "`";

	private final static Logger LOGGER = LoggerFactory.getLogger(DBManager.class);
	
	private static final String SQL_PATH = "/usr/share/aon-master";
	
	public static final String AON_MASTER = "aon_master";
	
	private static final String CREATE_SQL_PREFIX = "create.database.";
	
	private static final String CREATE_SQL = CREATE_SQL_PREFIX + "4.9.0.sql";
	
	private static final String INSERT_SQL = "default-insert.database.sql";
	
	private URL createSql;
	
	private URL defaultInsertSql;
	
	public DBManager() {
		init();
	}
	
	private File getCreateSqlFile( File path ) {
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return StringUtils.startsWithIgnoreCase(name, CREATE_SQL_PREFIX);
			}
			
		};
		File[] files = path.listFiles(filter);
		if (! ArrayUtils.isEmpty(files) ) {
			Arrays.sort( files );
			return files[files.length-1];
		}
		return null;
	}
	
	private URL toURL( File file ) {
		if ( file.exists() && file.isFile() && file.canRead() ) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				LOGGER.error( "Error calculating path of " + file, e );
			}
		}		
		return null;
	}
	
	private void init() {
		File path = new File( SQL_PATH );
		if ( path.exists() && path.isDirectory() && path.canRead() ) {
			createSql = toURL( getCreateSqlFile(path) );
			defaultInsertSql = toURL( new File(path, INSERT_SQL) );
		}
		if ( createSql == null ) {
			createSql = DBManager.class.getResource(CREATE_SQL);	
		} 
		if ( defaultInsertSql == null ) {
			defaultInsertSql = DBManager.class.getResource(INSERT_SQL);	
		}
	}
	
	private URL getCreateSqlURL() {
		return createSql;
	}

	private URL getInsertSqlURL() {
		return defaultInsertSql;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> readSqlScript( URL url, String dbName ) throws IOException {
		InputStream in = url.openStream();
		List<String> lines = IOUtils.readLines( in, "ISO-8859-1" );
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
		LOGGER.info( "Executing script {} for {}", scriptUrl, dbc);
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
			LOGGER.error(e.getMessage(), e);
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
			LOGGER.info( "Executing sql: {}", sql );
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
			LOGGER.info( "Check if exists database: {}", sql );
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
