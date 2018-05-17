package net.aonsolutions.core.dbutils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonSQLScript {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonSQLScript.class.getName());
	
	private static final String MSG1 = " row(s) updated/inserted.";
		
	private AonSQLFile file;
	private Connection c;
	private Integer domain;
	
	public AonSQLScript(AonSQLFile file, Connection c) {
		this.file = file;
		this.c = c;
	}

	public AonSQLFile getFile() {
		return file;
	}

	public Connection getConnection() {
		return c;
	}
	
	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	private void setDomainVariable() throws SQLException {
		if ( getDomain() != null ) {
			execute( "SET @Domain = " + getDomain() );
		}
	}

	private void execute( String sql ) throws SQLException {
		Statement statement = null;
		try {
			statement = getConnection().createStatement();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(sql);
			}
			int result = statement.executeUpdate(sql);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(result + MSG1);	
			}			
		} finally {
			DbUtils.closeQuietly(statement);
		}

	}
	
	public void execute() throws AonSQLException {
		boolean autoCommit = false;
		boolean autoCommitChanged = false;
		try {
			autoCommit = getConnection().getAutoCommit();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initial AutoCommit mode=[" + autoCommit+"]");
			}
			getConnection().setAutoCommit( false );
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("AutoCommit set to false.");
			}
			autoCommitChanged = true;
			setDomainVariable();
			while (getFile().ready()) {
				String stmt = getFile().getStatement();
				if ( stmt != null ) {
					execute(stmt);
				}
			}
			getFile().close();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Trying to commit . :o ");	
			}
			getConnection().commit();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Commit! :)");
			}
		} catch (SQLException e) {
			try {
				LOGGER.info("Trying to rollback . :(");
				getConnection().rollback();
			} catch (SQLException e1) {
				LOGGER.info("Rollback failed . :(");
			}
			StringBuffer sw = new StringBuffer();
			if (!StringUtils.isEmpty( getFile().getFileName())) {
				sw.append(" File: ");
				File file = new File(getFile().getFileName());
				sw.append(file.getName());
			}
			sw.append(" Line: ").append( getFile().getLineNumber() ).append(": ");
			sw.append( e.getMessage() );
			throw new AonSQLException(sw.toString() , e);
		} finally {
			if (autoCommitChanged) {
				try {
					getConnection().setAutoCommit( autoCommit );
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("AutoCommit set to " + autoCommit + ".");
					}
				} catch (SQLException e) {
					throw new AonSQLException("Unable to set AutoCommit to " + autoCommit,e);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String url = "jdbc:mysql://volga:3306/mysql";
		String user = "dbuser";
		String password = "serubd2000";
		
		Connection connection  = null ;
		try {
			connection = DriverManager.getConnection(url, user, password);
			File file = new File("/tmp/t4/aimar-esferalia-com.sql");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			AonSQLFile sqlFile = new AonSQLFile(in, CharEncoding.ISO_8859_1);
			AonSQLScript script = new AonSQLScript(sqlFile, connection);
			script.execute();
			sqlFile.close();
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

}
