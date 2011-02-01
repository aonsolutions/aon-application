package com.code.aon.dbutils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonSQLScript {

	private static final Logger LOGGER = LoggerFactory.getLogger(AonSQLScript.class.getName());
	
	private static final String MSG1 = " row(s) updated/inserted.";
		
	private AonSQLFile file;
	private Connection c;
	
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
			while (getFile().ready()) {
				String stmt = getFile().getStatement();
				if ( stmt != null ) {
					Statement s = getConnection().createStatement();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(stmt);
					}
					int result = s.executeUpdate(stmt);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(result + MSG1);	
					}
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
			sw.append(" Line: ");
			sw.append( getFile().getLineNumber() );
			sw.append(": ");
			sw.append(e.getMessage());
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

}
