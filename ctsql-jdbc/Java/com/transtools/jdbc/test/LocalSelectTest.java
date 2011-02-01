package com.transtools.jdbc.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFrame;


public class LocalSelectTest {


  private static final String DRIVER = "com.transtools.jdbc.CtsqlJdbcDriver";
  public static final String URL = "jdbc:ctsql://172.16.100.20:20011/mufaceml;DBPATH=c:\\tmp\\mufaceml;RTRIMCHAR=true";
  public static final String LOGIN = "ctsql";
  public static final String PASSWD = "ctsql";



	public static void run() throws ClassNotFoundException, SQLException{
		Connection con;
		Properties props = new Properties();
		Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");
		
		props = new java.util.Properties();
		props.setProperty( "user", "ctsql" );
		props.setProperty( "password", "ctsql" );
		con = DriverManager.getConnection( URL, props );
		
		executeStatement(con, "select last_insert_id()");
	}

	private static void executeStatement(Connection connection, String stmt) {
		Statement statement=null;
		boolean exist = false;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(stmt);
			while(resultSet.next()){
				int i = resultSet.getInt(1);
				System.out.println(i);
			}
			resultSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

  public static void main(String[] arg) {
	try {
		run();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
  }

}
