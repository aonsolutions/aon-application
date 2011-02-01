/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.jdbc.test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

/**
 *  Clase de testeo del driver JDBC 2.0
 *
 * @author     pedro dulce
 * @created    September 14, 2001
 * @version    $Revision: 1.4 $
 */
public class TestConnection extends TestCase {
	private static final String DRIVER = "com.transtools.jdbc.CtsqlJdbcDriver";
	private static final String DBPATH = "C:\\databases";
	private static final String HOST = "localhost";
	private static final String PORT = "20000";
	private static final String URL = "jdbc:ctsql://pedro:20000/prueba;DBPATH=c:\\databases\\prueba";
	private static final String LOGIN = "usulocal";
	private static final String PASSWD = "usulocal";
	private static final Connection conn;
	private static final String  DATABASENAME = "prueba";
	private static final java.util.Properties props;


	static{
		props = new Properties();
		props.setProperty("DBPATH", DBPATH);
		props.setProperty("user", LOGIN);
		props.setProperty("password", PASSWD);
		conn = SqlHelper.getConnection(HOST, PORT, DATABASENAME, props);

	}
	/**
	 *  Constructor for the <code>TestConnection</code> object
	 *
	 * @param  name
	 */
	public TestConnection(String name) {
		super(name);
	}

	/**
	 *  <OJO - Revisar> A unit com.transtools.jdbc.test for JUnit
	 */
	public void testConnect() {

		Statement st = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			sqlQuery = "CREATE TABLE Personal (member SMALLINT NOT NULL,";
			sqlQuery += "name CHAR(8),  inscriptionDate DATE, age INTEGER, primary key(member))";
			System.out.println("la query es: " + sqlQuery);
			rs= st.executeQuery("select * from systables");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			while (rs.next()) {
				System.out.println(rs.getString("tabname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
		String creaTablaPersonal = "CREATE TABLE Personal (member SMALLINT NOT NULL, name CHAR(8),  inscriptionDate DATE, age INTEGER, primary key(member))";

		rs = st.executeQuery(creaTablaPersonal);
		ResultSetMetaData rsmd = rs.getMetaData();

		int numberOfColumns = rsmd.getColumnCount();
		int rowCount = 1;
		while (rs.next()) {
			System.out.println("Row " + rowCount + ":  ");
			for (int i = 1; i <= numberOfColumns; i++) {
				System.out.print("   Column " + i + ":  ");
				System.out.println(rs.getString(i));
			}
			System.out.println("");
			rowCount++;
		}//while

		st.close();
		conn.close();

	} catch(SQLException ex) {
		System.err.print("SQLException: ");
		System.err.println(ex.getMessage());
	}
	try {
		rs.close();

		rs = null;
		st = null;
		conn.close();
		//conn = null;
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
}
