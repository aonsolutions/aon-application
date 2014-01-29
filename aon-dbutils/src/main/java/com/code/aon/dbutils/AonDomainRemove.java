package com.code.aon.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainRemove implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainRemove.class);
	
	private Connection connection;
	
	public AonDomainRemove(Connection connection) throws AonSQLException {
		this.connection = connection;
	}

	private void executeStatement( String statement ) {
		Statement s = null;
		try {
	        s = connection.createStatement();
	        s.execute(statement);			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(s);
		}
	}

	private int executeUpdate( String statement ) {
		Statement s = null;
		try {
	        s = connection.createStatement();
	        return s.executeUpdate(statement);			
		} catch (SQLException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(s);
		}
		return 0;
	}
	
	private void deleteFromTables( Integer domain ) {
		QueryRunner run = new QueryRunner();
		try {
			String dataBaseName = connection.getCatalog();
			ResultSetHandler<List<String>> h = new ColumnListHandler<String>();
			List<String> result = run.query( connection,
					"SELECT T.TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS as T " +
					"WHERE T.TABLE_SCHEMA = ? AND T.COLUMN_NAME = ?", h, dataBaseName, DOMAIN_COLUMN_NAME);
			if ( result != null ) {
				for( String table : result ) {
					int rows = executeUpdate( "DELETE FROM " + table + " WHERE domain = " + domain );
					if ( rows > 0 ) {
						LOGGER.info("Deleting {} table {} rows", table, rows);	
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	public void execute(Integer domain) throws AonSQLException {
		try {
            connection.setAutoCommit(false);
            
            executeStatement(SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.debug("Claves refereciales deshabilitadas");
            
            deleteFromTables(domain);
            
            int rows = executeUpdate( "DELETE FROM domain WHERE id = " + domain );
            LOGGER.info("Deleting DOMAIN table {} rows", rows);
            
            connection.commit();

		} catch (Throwable e) {
			try {
	            DbUtils.rollback(connection);	
			} catch ( SQLException sqle ) {
				LOGGER.error( sqle.getMessage(), sqle );
			}
			if ( e instanceof AonSQLException ) {
				throw (AonSQLException) e;
			}
			throw new AonSQLException(e.getMessage() , e);
		} finally {
			executeStatement(SET_FOREIGN_KEY_CHECKS_1);
			LOGGER.debug("Claves refereciales habilitadas");
		}
	}
	
	public static void main(String[] args) {
		DbUtils.loadDriver("org.gjt.mm.mysql.Driver");
		
		Integer domain = 7;
		
		String url = "jdbc:mysql://192.168.2.222:3306/aimar-esferalia-com";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			AonDomainRemove adr = new AonDomainRemove(connection);
			adr.execute(domain);
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
}