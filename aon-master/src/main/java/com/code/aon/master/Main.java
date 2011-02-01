package com.code.aon.master;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

	private static class Arguments
	{
		private String url;
		private String user;
		private String password;
		private String driver;

		
	}
	
	private static class IllegalArgumentsException
		extends RuntimeException
	{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection connection = null;
		try {
			Arguments arguments = parseArgs(args);
			VersionManager manager = new VersionManager();
			Class.forName(arguments.driver);
			connection = DriverManager.getConnection(arguments.url,
					arguments.user, arguments.password);
			manager.uptodateDatabase(connection);
		} catch ( IllegalArgumentsException e ){
			printUsage(args);
			System.exit(1);
		} catch ( Throwable th ) {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
			System.err.printf( "%s:%s\r\n" ,th.getClass().getName(), th.getMessage());
			System.exit(-1);
		}
	}


	private static void printUsage(String[] args)
	{
		System.err.printf("Usage : %s url user password driver \r\n", 
				Main.class.getName());
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
		
		return arguments;
	}

}
