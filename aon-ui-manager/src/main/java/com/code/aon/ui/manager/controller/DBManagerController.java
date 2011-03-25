package com.code.aon.ui.manager.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.DAOConstantsResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.master.VersionManager;
import com.code.aon.ui.manager.BeanManagerEx;
import com.code.aon.ui.util.AonUtil;

public class DBManagerController implements IManagerConstants {

	private static final String DB_SEP = "`";

	private final static Logger LOGGER = LoggerFactory.getLogger(DBManagerController.class);
	
	private VersionManager versionManager;
	
	private SessionFactory sessionFactory;
	
	private DBConnnection dbConnection;
	
	public DBManagerController() {
		this.versionManager = new VersionManager();
	}
	
	private Connection getConnection( DBConnnection dbc ) throws SQLException {
		DbUtils.loadDriver(dbc.getDriverClassName());
		String url = StringUtils.substringBeforeLast(dbc.getLabeledURI(), "/") + "/mysql";
	    Connection connection = DriverManager.getConnection(url, dbc.getUid(), dbc.getUserPasswordString() );
	    return connection;
	}
	
	private void removeDBConnnection( DBConnnection dbc ) throws SQLException {
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

	private boolean test( DBConnnection dbc ) throws SQLException {
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
	
	private boolean existsTable( DBConnnection dbc, String ... tableNames ) {
		boolean exists = false;
		Connection connection = null;
	    ResultSet tables = null;
		try {
			connection = getConnection(dbc);
			DatabaseMetaData dbm = connection.getMetaData();
			for( String tableName : tableNames ) {
				tables = dbm.getTables(dbc.getDBName(), "", tableName, null);
				exists = tables.next();
				DbUtils.closeQuietly(tables);
				if (! exists ) {
					break;
				}				
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(tables);
			DbUtils.closeQuietly(connection);
		}
		return exists;
	}		
	
	private void createDBConnnection( DBConnnection dbc ) throws AonException {
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

	private void insertDBConnnectionDefaults( DBConnnection dbc, String application ) throws AonException {
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

	public void createDB( DBConnnection dbConnection ) {
		if (! exists(dbConnection) ) {
			try {
				createDBConnnection(dbConnection);
			} catch (AonException e) {
				LOGGER.error(e.getMessage(), e);
				try {
					removeDB(dbConnection);
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
				throw new AbortProcessingException( e.getMessage(), e );
			}
		} else {
			AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DB_DUPLICATED, dbConnection.getDBName());
		}
	}
	
	public void removeDB( DBConnnection dbConnection ) throws SQLException {
		if ( exists(dbConnection) ) {
			removeDBConnnection(dbConnection);
		}
	}		
	
	public void insertDefaults( DBConnnection dbConnection, String application ) throws AonException {
		if ( exists(dbConnection) ) {
			insertDBConnnectionDefaults(dbConnection, application);
		} else {
			AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DB_NOT_EXIST, dbConnection.getDBName());
		}
	}
		
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public DBConnnection getCurrentDBConnection() {
		return this.dbConnection;
	}
		
	private SessionFactory initSessionFactory( DBConnnection dbc ) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		dbc.configure(configuration);
   		configuration.buildMappings();
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		BeanManagerEx.getInstance().update(sessionFactory);
        DAOConstantsResolver resolver = new DAOConstantsResolver(configuration);
        resolver.createDAOConstants();
        return sessionFactory;
	}
	
	public boolean changeDbConnection(DBConnnection dbc) {
		if (! dbc.equalsDB(this.dbConnection) ) {
			this.dbConnection = dbc;	
			if ( this.sessionFactory != null ) {
				this.sessionFactory.close();
			}
			this.sessionFactory = initSessionFactory(this.dbConnection);				
			return true;
		}
		return false;
	}


	public boolean isAonDB( DBConnnection dbc ) {
		return existsTable(dbc, USER_TABLE, SCOPE_TABLE, WORK_GROUP_TABLE, COMPANY_TABLE);
	}
	
}
