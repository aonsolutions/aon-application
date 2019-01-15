/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written
* permission of esferalia NETWORKS, or in accordance with the
* terms and conditions stipulated in the agreement contract
* under which the program has been supplied.
*********************************************************************/

package com.code.aon.master;

import java.util.Properties;
import java.util.TimeZone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateDB {

	private static class Arguments
	{
		private String url;
		private String user;
		private String password;
		private String driver;
		private String dbName;
		private String timeZone;


	}

	private static class IllegalArgumentsException extends RuntimeException {

		private static final long serialVersionUID = 1L;

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

			Properties properties = new Properties();
			properties.setProperty("user", arguments.user);
			properties.setProperty("password", arguments.password);
			properties.setProperty("serverTimezone", arguments.timeZone);
			connection = DriverManager.getConnection(arguments.url, properties);

			manager.createDatabase(connection, arguments.dbName);
		} catch ( IllegalArgumentsException e ){
			printUsage(args);
			System.exit(-1);
		} catch ( Throwable th ) {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
			System.err.printf( "%s:%s\r\n" ,th.getClass().getName(), th.getMessage());
			System.exit(-2);
		}
	}


	private static void printUsage(String[] args)
	{
		System.err.printf("Usage : %s url user password driver \r\n",
				CreateDB.class.getName());
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
		arguments.dbName = args[4];
		arguments.timeZone = TimeZone.getDefault().getID();

		return arguments;
	}

}
