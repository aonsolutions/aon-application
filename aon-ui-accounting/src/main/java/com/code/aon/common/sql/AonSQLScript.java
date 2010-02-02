package com.code.aon.common.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class AonSQLScript {

	private static final Logger LOGGER = Logger.getLogger(AonSQLScript.class.getName());
	
	private static final String MSG1 = " row(s) updated/inserted.";
		
	private AonSQLFile file;
	private Connection c;

	public AonSQLScript(AonSQLFile file, Connection c) {
		this.file = file;
		this.c = c;
	}

	public void execute() throws AonSQLException {
		boolean autoCommit = false;
		boolean autoCommitChanged = false;
		try {
			autoCommit = c.getAutoCommit();
			LOGGER.info("Initial AutoCommit mode=[" + autoCommit+"]");
			c.setAutoCommit( false );
			LOGGER.info("AutoCommit set to false.");
			autoCommitChanged = true;
			while (file.ready()) {
				String stmt = file.getStatement();
				Statement s = c.createStatement();
				LOGGER.info(stmt);
				int result = s.executeUpdate(stmt);
				LOGGER.info(result + MSG1);
			}
			file.close();
			LOGGER.info("Trying to commit . :o ");
			c.commit();
			LOGGER.info("Commit! :)");
		} catch (SQLException e) {
			try {
				LOGGER.info("Trying to rollback . :(");
				c.rollback();
			} catch (SQLException e1) {
				LOGGER.info("Rollback failed . :(");
			}
			throw new AonSQLException("Line " + file.getLineNumber() + ": " + e.getMessage(), e);
		} finally {
			if (autoCommitChanged) {
				try {
					c.setAutoCommit( autoCommit );
					LOGGER.info("AutoCommit set to " + autoCommit + ".");
				} catch (SQLException e) {
					throw new AonSQLException("Line " + file.getLineNumber() + ": " + e.getMessage(), e);
				}
			}
		}
	}

}
