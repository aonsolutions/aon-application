package com.code.aon.master;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.TimeZone;

import net.aonsolutions.core.dbutils.AonSQLException;

public class Up2DateDB {

	private static class Arguments
	{
		private String url;
		private String user;
		private String password;
		private String driver;
		private String useSSL;
		private String timeZone;


	}

	private static class IllegalArgumentsException extends RuntimeException	{
		private static final long serialVersionUID = 1L;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection connection = null;
		Arguments arguments = parseArgs(args);
		VersionManager manager = new VersionManager();
		Properties properties = new Properties();
		properties.setProperty("user", arguments.user);
		properties.setProperty("password", arguments.password);
		properties.setProperty("useSSL", arguments.useSSL);
		properties.setProperty("serverTimezone", arguments.timeZone);
		try {
			Class.forName(arguments.driver);
			connection = DriverManager.getConnection(arguments.url,properties);

			manager.uptodateDatabase(connection);

		} catch ( IllegalArgumentsException e ){
			System.err.printf( "%s:%s\r\n" ,e.getClass().getName(), e.getMessage());
			printUsage(args);
			System.exit(1);
		}  catch ( AonSQLException e ){
			Collection<String> schemas = Collections.emptyList();
			try {
				schemas = manager.getSchemas(connection);
			} catch ( Throwable th ){
				System.err.printf( "%s:%s\r\n" ,th.getClass().getName(), th.getMessage());
				System.exit(-1);
			}
			int exit = 0; // OK
			for ( String schema : schemas ) {
				try {
					connection = DriverManager.getConnection(
							String.format("%s/%s",arguments.url,schema),properties);
					System.out.printf("Actualizando '%s'...", schema);
					manager.uptodateDatabase(connection);
					System.out.printf("OK\r\n");
					connection.close();
				} catch ( Throwable th) {
					System.out.printf("ERROR (%s)\r\n", th.getMessage());
					if (connection != null) {
						try {
							connection.close();
						} catch (SQLException _e) {
						}
					}
					exit = -1;
				}
			}
			System.exit(exit);
		}
		catch ( Throwable th ) {
			System.err.printf( "%s:%s\r\n" ,th.getClass().getName(), th.getMessage());
			System.exit(-1);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
	}


	private static void printUsage(String[] args)
	{
		System.err.printf("Usage : %s url user password driver \r\n",
				Up2DateDB.class.getName());
	}

	private static Arguments parseArgs(String[] args)
		throws IllegalArgumentsException
	{
		if ( args.length < 4 )
			throw new IllegalArgumentsException();

		Arguments arguments = new Arguments();

		arguments.url = args[0];
		arguments.user = args[1];
		arguments.password = args[2];
		arguments.driver = args[3];
		arguments.useSSL = "false";
		arguments.timeZone = TimeZone.getDefault().getID();

		return arguments;
	}

}
