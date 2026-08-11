package net.aonsolutions.db.up2date;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Up2DateUtils {
	private Up2DateUtils() {
	}
	
	
	public static boolean tableExists(Connection connection, Logger logger, String table) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select 1 from "+table+" limit 1");
			return true;
		} catch (Throwable t) {
			logger.severe("Table "+table+" no existe");
			return false;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			;
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			;
		}
		
	}
	
	public static boolean columnNotExists(Connection connection, Logger logger, String table, String column) {
		return !columnExists(connection, logger, table, column);
	}
	public static boolean columnExists(Connection connection, Logger logger, String table, String column) {
		if (tableExists(connection, logger, table)) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = connection.createStatement();
				rs = stmt.executeQuery("select "+column+" from "+table+" limit 1");
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String name = rsmd.getColumnName(i);
					
					if (column.equals(name)) {
						return true;
					}
				}
				return false;
			} catch (Throwable t) {
				logger.info("Column "+column+" no existe en la tabla "+table);
				return false;
			} finally {
				if (stmt != null)
					try {
						stmt.close();
					} catch (SQLException e) {
					}
				;
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
					}
				;
			}
		} else {
			logger.severe("Table "+table+" does not exist");
			return false;
		}
	}



}
