package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CtsqlJdbcTest
{

	private static void registerDriver(String driver)
	{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: "); 
			System.err.println(e.getMessage());
		}
	}

	private static Connection getConnection(String url, String user, String password)
		throws SQLException 
	{
		return DriverManager.getConnection(url, user, password);
	}

	private static String createUrl
		(String dbHost, String dbService, String dbPath, String dbName)
	{
		String url = "jdbc:ctsql";
		
		url += "://" + dbHost;
		if ( (dbService != null) && (dbService.length() > 0) )
			url += ":" + dbService;
			
		url += "/" + dbName;
		url += ";DBPATH=" + dbPath;

		return url;	
	}
	
	public static void main(String args[])
	{
		  
		String driver = "com.transtools.jdbc.CtsqlJdbcDriver";
		String dbService = "20000";
		String dbHost = "localhost";
		String dbPath = "c:\\cosmos\\projets\\almafac";
		String dbName = "almafac";
		String dbUser = "prueba";
		String dbPassword = "prueba";

		registerDriver( driver );
		String url = createUrl( dbHost, dbService, dbPath, dbName );
		
		try {
			System.out.println( "Creating connecction to " + url + " ..." );
			Connection connection = getConnection( url, dbUser, dbPassword );
	
			//  

			connection.close();
			System.out.println( "Done !!!" );			
	
		} catch(SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}

}
