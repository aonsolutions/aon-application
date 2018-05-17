package net.aonsolutions.core.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonDomainRemove implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(AonDomainRemove.class);
	
	private Connection connection;
	
	public AonDomainRemove(Connection connection) throws AonSQLException {
		this.connection = connection;
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
					String statement = "DELETE FROM " + table + " WHERE domain = " + domain;
					int rows = TableUtil.executeUpdate( connection, statement );
					if ( rows > 0 ) {
						LOGGER.info("Deleting {} table {} rows", table, rows);	
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	public boolean execute(Integer domain) throws AonSQLException {
		boolean domainDeleted = false;
		try {
			LOGGER.info("Database {}, domain {}", connection.getMetaData().getURL(), domain);
			DomainInfo domainInfo = TableUtil.getDomainInfo(connection, domain);
			LOGGER.info("{}", domainInfo);
			
            connection.setAutoCommit(false);
            
            TableUtil.executeStatement( connection, SET_FOREIGN_KEY_CHECKS_0);
            LOGGER.debug("Claves refereciales deshabilitadas");
            
            deleteFromTables(domain);
            
            String statement = "DELETE FROM domain WHERE id = " + domain;
            int rows = TableUtil.executeUpdate( connection, statement );
            LOGGER.info("Deleting DOMAIN table {} rows", rows);
            domainDeleted = (rows == 1);
            
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
			TableUtil.executeStatement(connection, SET_FOREIGN_KEY_CHECKS_1);
			LOGGER.debug("Claves refereciales habilitadas");
		}
		return domainDeleted;
	}
	
	public static void main(String[] arguments) {
		DomainCommandLine dcl = new DomainCommandLine();
		
		dcl.parse(AonDomainRemove.class.getName(), arguments);
				
		Connection connection = null;
		try {
			connection = dcl.getConnection();
			
			Integer[] domains = dcl.getDomains(connection);
			if (! ArrayUtils.isEmpty(domains) ) {
				LOGGER.info( "Starting process..." );
				AonDomainRemove adr = new AonDomainRemove(connection);
				for(Integer domain : domains) {
					adr.execute(domain);
				}
			}
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}	
	
}