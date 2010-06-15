package com.code.aon.master;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.code.aon.dbutils.AonSQLException;

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
		
		try {
			Arguments arguments = parseArgs(args);
			VersionManager manager = new VersionManager();
			System.out.printf("Updating database '%s'...", arguments.url );
			Class.forName(arguments.driver);
			Connection connection = 
				DriverManager.getConnection(arguments.url,
					arguments.user,
					arguments.password);
				manager.uptodateDatabase(connection);
		} catch ( IllegalArgumentsException e ){
			printUsage(args);
		} catch (Exception e) {
			System.out.printf( "FAILURE (%s:%s)\r\n" ,e.getClass().getName(),  e.getMessage());
		}
		System.out.println("OK.");
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
