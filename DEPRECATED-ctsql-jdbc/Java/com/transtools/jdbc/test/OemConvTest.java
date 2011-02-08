/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.jdbc.test;
import com.transtools.ctsql.impl.OemConverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

/**
 *  <OJO - Put here the class description>
 *
 * @author     Miguel Angel Guijarro
 * @created    7 de noviembre de 2001
 * @version    $Revision: 1.3 $
 */
public class OemConvTest extends TestCase {
	private	Data data;
	protected Connection connection;

	/**
	 *  <OJO - Revisar> Constructor for the <code>OemConvTest</code> object.
	 *
	 * @param  name  <OJO - Put here the parameter description>
	 */
	public OemConvTest(String name) {
		super(name);
	}

	protected void setUp()
	{
		data=new Data();
		registerDriver();
		openConnection();
	}

	protected void tearDown()
	{
		try
		{
			connection.close();
		}catch(Throwable ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	public void testOemConv() {
		System.out.println(new String(OemConverter.AnsiToOem("áéíóúÑñÁÉÍÓÚ".getBytes())));
		System.out.println(new String(OemConverter.OemToAnsi(OemConverter.AnsiToOem("áéíóúÑñÁÉÍÓÚ".getBytes()))));

		assertTrue(new String(OemConverter.OemToAnsi(OemConverter.AnsiToOem("áéíóúÑñÁÉÍÓÚ".getBytes()))).compareTo("áéíóúÑñÁÉÍÓÚ") == 0);
		try{
			Statement stmt = connection.createStatement();
			stmt.execute("delete from oem;");
			stmt.close();
			stmt.execute("insert into oem values('áéíóúÑñÁÉÍÓÚ');");
			stmt.close();
			ResultSet rs = stmt.executeQuery("select * from oem");
			assertNotNull("Result Set", rs);
			assertTrue("There are data", rs.next());
			assertTrue("Data is not equal", rs.getString(1).trim().compareTo("áéíóúÑñÁÉÍÓÚ") == 0);
		}catch (SQLException e){
			fail("Unexpected SQLException: " + e.getMessage());
		}
	}

	private Class registerDriver()
	{
		java.lang.Class obj = null;
		try
		{
			obj = Class.forName(data.driver);
		}catch(ClassNotFoundException ex)
		{
			System.out.println(ex.getMessage());
			fail("CtsqlStatementTest. RegisterDriver method. Driver not found ");
		}
		return obj;
	}

	private void openConnection()
	{
		try
		{
			String url = data.createUrl();
			java.util.Properties info = new java.util.Properties();
			if (data.user != null) {
				info.put("user", data.user);
			}
			if (data.password != null) {
				info.put("password", data.password);
			}
			info.put("DBCHARSET", "OEM");
			connection = DriverManager.getConnection(url, info);
			assertNotNull(connection);
		}catch(SQLException ex)
		{
			fail("CtsqlStatementTest. OpenConnection private method. Unexpected SQLException: " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. OpenConnection private method. Unexpected Exception: " + th.getMessage());
		}
	}

}
