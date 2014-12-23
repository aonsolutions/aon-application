package com.esferalia.aon.payroll.calculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class AbstractSQLTestCase {

	private Connection connection;


	private static String getDbPort(){
		return System.getProperty("dbPort", "3306");
	}

	private static String getDbHost(){
		return System.getProperty("dbHost", "127.0.0.1");
	}

	private static String getDbName(){
		return System.getProperty("dbName", "aon-master");
	}

	private static String getDbUser(){
		return System.getProperty("dbUser", "dbuser");
	}

	private static String getDbPasswd(){
		return System.getProperty("dbPasswd", "serubd2000");
	}
	
	public Connection getConnection() {
		return connection;
	}

	@Before
	public void setUp() throws ClassNotFoundException, SQLException{
		// first of all load JDBC driver
		Class.forName("org.gjt.mm.mysql.Driver");
		
		String dbHost = getDbHost();
		String dbPort = getDbPort();
		String dbName = getDbName();
		String dbUser = getDbUser();
		String dbPasswd = getDbPasswd();
		
		String url = String.format("jdbc:mysql://%s:%s", dbHost, dbPort,dbName);
		connection = DriverManager.getConnection(url, dbUser, dbPasswd);
		
		ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");
		while( rs.next()) {
			if ( rs.getString(1).startsWith(dbName) ) {
				connection.createStatement().execute("use " + rs.getString(1) );
			}
		}
	}
	
	@After
	public void tearDown() throws SQLException{
		if (connection != null)
			connection.close();
	}
	

}
