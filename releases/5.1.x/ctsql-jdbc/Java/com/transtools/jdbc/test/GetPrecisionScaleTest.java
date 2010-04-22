package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class GetPrecisionScaleTest extends TestCase{

	private String tableName = "gpst";
	protected Connection connection;

	public GetPrecisionScaleTest(String name)
	{
		super(name);
	}

	protected void setUp()
	{
		connection = SqlHelper.getConnection();
		assertNotNull( connection );

		if ( existsTable(tableName) )
		dropTable( tableName );

		createTable( tableName );
	}

	protected void tearDown()
	{
		dropTable( tableName );
		try
		{
			connection.close();
		} catch(SQLException ex) {
			fail(ex.getMessage());
		}
	}

	public void testGetPrecision() {
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;

		try
		{
			stmt = connection.createStatement();
			rs =stmt.executeQuery( "SELECT * FROM "+tableName );
			rsm = rs.getMetaData();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}

		assertNotNull( rsm );

		try
		{
			assertTrue( "Incorrect precision for d1", 17 == rsm.getPrecision(1) );
			assertTrue( "Incorrect precision for d2", 32 == rsm.getPrecision(2) );
			assertTrue( "Incorrect precision for c", 517 == rsm.getPrecision(3) );
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}

		try
		{
			rs.close();
			stmt.close();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}
	}

	public void testGetScale() {
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;

		try
		{
			stmt = connection.createStatement();
			rs =stmt.executeQuery( "SELECT * FROM "+tableName );
			rsm = rs.getMetaData();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}

		assertNotNull( rsm );

		try
		{
			assertTrue( "Incorrect scale for d1", 11 == rsm.getScale(1) );
			assertTrue( "Incorrect scale for d2", 9 == rsm.getScale(2) );
			assertTrue( "Incorrect scale for c", 0 == rsm.getScale(3) );
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}

		try
		{
			rs.close();
			stmt.close();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}
	}

	private boolean existsTable( String tableName ) {
		boolean exist = false;

		try
		{
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM SYSTABLES WHERE tabname = '" + tableName + "'" );
			exist = rs.next();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}

		return exist;
	}

	private void createTable( String tableName ) {
		try
		{
			Statement stmt = connection.createStatement();

			stmt.execute( "CREATE TABLE "+tableName+" (d1 DECIMAL(17,11), d2 DECIMAL(32,9), c CHAR(517) )" );
			stmt.close();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}
	}

	private void dropTable( String tableName ) {
		try
		{
			Statement stmt = connection.createStatement();

			stmt.execute( "DROP TABLE "+tableName );
			stmt.close();
		} catch ( SQLException se ) {
			fail( se.getMessage() );
		}
	}

	public static void main (String[] args) {
		GetPrecisionScaleTest gpst = new GetPrecisionScaleTest( "gpst" );

		gpst.setUp();
		gpst.testGetPrecision();
		gpst.testGetScale();
		gpst.tearDown();

		System.out.println( "Done !!" );
	}

}
