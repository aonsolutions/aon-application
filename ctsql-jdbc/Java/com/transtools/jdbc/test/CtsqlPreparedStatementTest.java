package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
//import com.transtools.ctsql.CtsqlType;
//com.transtools.jdbc.test.CtsqlPreparedStatementTest
public class CtsqlPreparedStatementTest extends TestCase{

	protected Connection connection;
	protected ResultSet resultSet=null;
	protected String driver;
	protected String protocol;
	protected String host;
	protected String dbpath;
	protected String dbname;
	protected String url;
	protected String user;
	protected String password;
	private int port = 20000;

	public CtsqlPreparedStatementTest(String name)
	{
	  super(name);
	}

	protected void setUp()
	{
		Data datos=new Data();

		driver = datos.driver;
		protocol = datos.protocol;
		host = datos.host;
		dbpath = datos.dbpath;
		dbname = datos.dbname;
		user = datos.user;
		password = datos.password;

		registerDriver();
		openConnection();
				createDatabase();
//		createTable();
//		System.out.println("1.	ha creado las tablas");
	}

			public void createDatabase(){
		PreparedStatement pstmt=null;
		boolean b=false;

		try
			{
				pstmt=executeStatement("create database test");
				b = pstmt.execute();
			} catch (SQLException se) {
				fail("CtsqlPreparedStatementTest. createDatabase method . Unexpected throwable " +
				se.getMessage());
			}
			catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. createDatabase method . Unexpected throwable " +
				ex.getClass());
			}
	}


	public void createTable(){
		PreparedStatement pstmt=null;
		boolean b=false;

		try
			{
				pstmt=executeStatement("create table clave (sm Smallint,car char(20))");
				b = pstmt.execute();
				pstmt=executeStatement("create table basic(sm Smallint not null label 'Smallint',ch char(20) label 'char',int Integer label 'integer',dec Decimal label 'decimal',dt Date label 'date',tm Time label 'time')primary key(sm)");
				b = pstmt.execute();
			} catch (SQLException se) {
				fail("CtsqlPreparedStatementTest. createTable method . Unexpected throwable " +
				se.getMessage());
			}
			catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. createTable method . Unexpected throwable " +
				ex.getClass());
			}
	}

	private void dropTable(){
		PreparedStatement pstmt=null;
		boolean b=false;

		pstmt=executeStatement("drop table clave");
		try	{
				b=pstmt.execute();
		} catch (SQLException se) {
				fail("1.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
				se.getMessage());
		} catch (Throwable ex) {
			fail("2.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
				ex.getMessage());
		}
		try	{
				pstmt=executeStatement("drop table basic");
				pstmt.execute();
		} catch (SQLException se) {
				fail("1.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
				se.getMessage());
		} catch (Throwable ex) {
			fail("2.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
				ex.getMessage());
		}

	}

	public PreparedStatement executeStatement(String cad){

	PreparedStatement pstmt=null;

	  try {
			pstmt = connection.prepareStatement(cad);
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. createStatement method . SQLException ",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. createStatement method . Unexpected throwable " +
				ex.getMessage());
		}
		return pstmt;
	}

	private void close_resultSet()
	{
		try {
			resultSet.close();
		} catch (SQLException ex) {
			fail("CtsqlPreparedStatementTest. Method: close_resultSet. The resultSet can not be closed");
		}
		catch (Throwable th) {
			fail("CtsqlPreparedStatementTest. Method: close_resultSet. A Throwable exception shouldn't be thrown " +
				th.getClass() + " with message " + th.getMessage());
		}
	}

	protected void tearDown()
	{
		try
		{
//			  dropTable();
//				System.out.println("4.	después de eliminar las tablas");
				connection.close();
		}catch(Throwable ex)
		{
			System.out.println(ex.getMessage());
		}
	}


	public void testExecute()
	{
		PreparedStatement pstmt=null;
		boolean b=true;
		String Nombre=null;
		String descripcion=null;
		short sm=0;
		int in=0;
		double d=0.0;
		GregorianCalendar gc = new GregorianCalendar(00,00,00);
		java.sql.Date dt= new Date(gc.getTime().getTime()) ;
		java.sql.Time tm=new Time(0);

		int i=0;

		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (110,'Maria',-2147483647,22.56,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//			pstmt=executeStatement("select * from basic");
//			b = pstmt.execute();
//			pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (400,'Maria',20,22.56,'06/14/2000','4:30:00')");
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			se.getMessage());
//		}
//		catch (Throwable ex) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex.getClass());
//		}

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)110);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

	try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}

		try
		{
		b=resultSet.next();
		} catch (SQLException se) {
			fail("CtslPreparedStatementTest. testExecute: The throwable object should be an SQLException"+se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b,b);

	try
		{
		b=resultSet.next();
		} catch (SQLException se) {
			assertNotNull("CtslPreparedStatementTest. testExecute: The throwable object should be an SQLException"+se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}

		close_resultSet();


		try
		{
		connection.close();
			openConnection();
			pstmt=executeStatement("select * from basic where int = ?");

		pstmt.setInt(1,20);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try
		{
		b=resultSet.next();
		} catch (SQLException se) {
			fail("CtslPreparedStatementTest. testExecute: The throwable object should be an SQLException"+se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b,b);

		try
		{
		b=resultSet.next();
		} catch (SQLException se) {
			fail("CtslPreparedStatementTest. testExecute: The throwable object should be an SQLException"+se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b,(!b));

		close_resultSet();
		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setInt(1,66);
			b = pstmt.execute();
			pstmt.close();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		pstmt=executeStatement("select * from basi where int = ?");

	try
		{
			pstmt.setInt(1,20);
			b = pstmt.execute();
			pstmt.close();
		} catch (SQLException se) {
				assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable ",se);
	}
		catch (Throwable ex3) {
			  assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex3.getClass());
		}

//	   MINIMO SHORT

		pstmt=executeStatement("insert into clave (sm,car) values (-32767,'Maria')");
		try
		{
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setShort(1,(short)-32767);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				sm = resultSet.getShort(1);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + sm, new Short(sm), new Short((short)-32767));

		close_resultSet();
		 pstmt=executeStatement("delete from clave where sm =-32767");
		try
		{
			i=pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	   MAXIMO SHORT
		pstmt=executeStatement("insert into clave(sm,car)values(32767,'yo')");
		try
		{
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setShort(1,(short)32767);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				sm = resultSet.getShort(1);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + sm, new Short(sm), new Short((short)32767));

		close_resultSet();
			pstmt=executeStatement("delete from clave where sm =32767");
		try
		{
			i=pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	  SHORT CERO
		pstmt=executeStatement("insert into clave(sm,car)values(0,'yo')");
		try
		{
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setShort(1,(short)0);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				sm = resultSet.getShort(1);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + sm, new Short(sm), new Short((short)0));

		close_resultSet();
		pstmt=executeStatement("delete from clave where sm =0");
		try
		{
			i=pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	  MINIMO INTEGER
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (550,'Maria',-2147483647,2.56789,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setInt(1,-2147483647);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				in = resultSet.getInt(3);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + in, new Integer(in), new Integer(-2147483647));

//  	pstmt=executeStatement("delete from clave where int = -2147483647");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	  MAXIMO INTEGER
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (870,'Maria',2147483647,2.56789,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (200,'Maria',2147483647,2.57,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//			pstmt=executeStatement("select * from basic where int = ?");
//			pstmt.setInt(1,2147483647);
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			se.getMessage());
//		}
//		catch (Throwable ex) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				in = resultSet.getInt(3);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + in, new Integer(in), new Integer(2147483647));

//  	pstmt=executeStatement("delete from basic where int = 2147483647");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	  INTEGER CERO
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (880,'Maria',0,0.10,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setInt(1,0);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				in = resultSet.getInt(3);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//		assertEquals("CtsqlPreparedStatementTest. Method: testExecute. The retrieved value is not correct " + in, new Integer(in), new Integer(0));

//	 	pstmt=executeStatement("delete from basic where int = 0");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}

//	  DECIMAL
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (800,'Maria',0,0.10,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from basic where dec = ?");
		try
		{
			pstmt.setDouble(1,2.57);
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
			boolean bool = resultSet.next();
			if (bool)
				d = resultSet.getDouble(4);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. " + th.getMessage());
		}

		assertEquals("CtsqlPreparedStatementTest.testSetDouble: The retrieved value isn't correct",new Double(2.57), new Double(d));


		pstmt=executeStatement("select * from basic where dec = ?");
		try
		{
			pstmt.setDouble(1,3.10);
			b = pstmt.execute();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}

//		pstmt=executeStatement("delete from clave where int = 0.000001");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	DATE
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (800,'Maria',0,2.56789,'01/01/1900','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			gc = new GregorianCalendar(01,01,00,01,01,00);
			pstmt.setDate(1,new Date(gc.getTime().getTime()));
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				dt = resultSet.getDate(5);
	  }catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//  	pstmt=executeStatement("delete from basic where dt = '01/01/1900'");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}

//	TIME
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (800,'Maria',0,2.56789,'01/01/1900','01:01:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, (!b));

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setTime(1,new Time(gc.getTime().getTime()));
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecute method . The resultSet shouldn't be null ", resultSet);

		try{
		  b=resultSet.next();
				tm = resultSet.getTime(6);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

//		pstmt=executeStatement("insert into clave(sm,car)values(333,'yo')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable ",se);
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}

//	  pstmt=executeStatement("delete from clave where sm = 666");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

		pstmt=executeStatement("insert into clave(sm,car)values(666,'tú')");
		try
		{
			i=pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecute method ", new Integer(i), new Integer(1));

//	  pstmt=executeStatement("delete from clav where sm = 666");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable ",se);
//		}
//		catch (Throwable ex4) {
//			assertNotNull("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}

//		pstmt=executeStatement("delete from basic where tm = '01:01:00'");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
	}

	public void testStringExecute()
	{
		PreparedStatement pstmt=null;
		boolean b=false;
		String Nombre=null;
		String descripcion=null;

		int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			b = pstmt.execute("select * from basic where sm = 110");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testStringExecute method. b=" +b, b);

		try
		{
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecute: The throwable object should be an SQLException");
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex.getClass());
		}

		assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . The resultSet shouldn't be null ", resultSet);

		try
		{
		b=resultSet.next();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecute: The throwable object should be an SQLException"+se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testStringExecute method. b=" +b,b);

		close_resultSet();

		try
		{
			connection.close();
			openConnection();
			pstmt=executeStatement("select * from basic where int = 20");

			b = pstmt.execute("select * from basic where int = ?");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex2.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testStringExecute method. b=" +b, b);

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			b = pstmt.execute("select * from basic where int = 66");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex2.getClass());
		}

		close_resultSet();
		pstmt=executeStatement("select * from basi where int = ?");
		try
		{
			b = pstmt.execute("select * from basi where int = 20");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex3) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex3.getClass());
		}

//		pstmt=executeStatement("insert into clave(sm,car)values(434,'yo')");
//		try
//		{
//			b = pstmt.execute("insert into clave(sm,car)values(434,'yo')");
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testStringExecute method. b=" +b, (!b));

		close_resultSet();
		pstmt=executeStatement("insert into clave(sm,car)values(404,'yo')");
		try
		{
			b = pstmt.execute("insert into clave(sm,car)values(404,'yo')");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex4.getClass());
		}

//	  pstmt=executeStatement("delete from clave where sm = 666");
//		try
//		{
//			i=pstmt.executeUpdate("delete from clave where sm = 666");
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testStringExecute method ", new Integer(i), new Integer(1));

		pstmt=executeStatement("insert into clave(sm,car)values(666,'tú')");
		try
		{
			i=pstmt.executeUpdate("insert into clave(sm,car)values(666,'tú')");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testStringExecute method ", new Integer(i), new Integer(1));

	  pstmt=executeStatement("delete from clav where sm = 666");
		try
		{
			i=pstmt.executeUpdate("delete from clav where sm = 666");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecute method . Unexpected throwable " +
				ex4.getClass());
		}

  }

	public void testExecuteQuery()
	{
		PreparedStatement pstmt=null;
		boolean b=false;


//
//		pstmt=executeStatement("insert into basic (sm,ch,int,dec,dt,tm) VALUES (220,'Maria',-2147483647,22.56,'06/14/2000','4:30:00')");
//		try
//		{
//			b = pstmt.execute();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			se.getMessage());
//		}
//		catch (Throwable ex) {
//			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
//     			ex.getClass());
//		}
//		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);


		pstmt=executeStatement("select * from basic where sm = '400'");
		try
		{
			resultSet=pstmt.executeQuery();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method ",resultSet);

		try
		{
		b=resultSet.next();
	  } catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecuteQuery method ",b);

		pstmt=executeStatement("select * from bas");
		try
		{
			resultSet=pstmt.executeQuery();

		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}

		pstmt=executeStatement("insert into clave(sm,car)values(987,'él')");
		try
		{
			resultSet=pstmt.executeQuery();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}


		try
		{
			connection.close();
			openConnection();
			pstmt=executeStatement("delete from clave where sm = 666");

			resultSet=pstmt.executeQuery();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}

		pstmt=executeStatement("insert into clave(sm,car)values(166,'tú')");
		try
		{
			resultSet=pstmt.executeQuery();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
	}

	public void testStringExecuteQuery()
	{
		PreparedStatement pstmt=null;
		boolean b=false;

		pstmt=executeStatement("select * from basic where sm = '400'");
		try
		{
			resultSet=pstmt.executeQuery("select * from basic where sm = '400'");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testStringExecuteQuery method ",resultSet);

		try
		{
		b=resultSet.next();
	  } catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testStringExecuteQuery method ",b);

		pstmt=executeStatement("select * from bas");
		try
		{
			resultSet=pstmt.executeQuery("select * from bas");

		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}

//    try
//		{
//		  connection.close();
//			openConnection();
//			pstmt=executeStatement("delete from clave where sm = 666");
//
//			resultSet=pstmt.executeQuery("delete from clave where sm = 666");
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable ",se);
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " +
//     			ex4.getClass());
//		}

		pstmt=executeStatement("insert into clave(sm,car)values(666,'tú')");
		try
		{
			resultSet=pstmt.executeQuery("insert into clave(sm,car)values(666,'tú')");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecuteQuery method . Unexpected throwable " +
				ex4.getClass());
		}
	}

	public void testExecuteUpdate()
	{
		PreparedStatement pstmt=null;
	boolean b=false;
		int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)110);
			i = pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecuteUpdate method. b=",new Integer(i),new Integer(1));

		try
		{
			resultSet=pstmt.getResultSet();  // devuelve null
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate: The throwable object should be an SQLException",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex.getClass());
		}

	  pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)400);   // existe más de un resultado
			i = pstmt.executeUpdate();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex.getClass());
		}

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setInt(1,20);  // más de un resultado
			i = pstmt.executeUpdate();
			} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex2.getClass());
		}

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			pstmt.setInt(1,66);
			i = pstmt.executeUpdate();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex2.getClass());
		}

		pstmt=executeStatement("select * from basi where int = ?");
		try
		{
			pstmt.setInt(1,20);
			i = pstmt.executeUpdate();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex3) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex3.getClass());
		}

		pstmt=executeStatement("insert into clave(sm,car)values(343,'yo')");
		try
		{
			i = pstmt.executeUpdate();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testL method . Unexpected throwable " +
				ex4.getClass());
		}

//		try
//		{
//		  connection.close();
//			openConnection();
//			pstmt=executeStatement("delete from clave where sm = 666");
//
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecuteUpdate method. b=",new Integer(i),new Integer(1));

		pstmt=executeStatement("insert into clave(sm,car)values(666,'tú')");
		try
		{
			i=pstmt.executeUpdate();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testExecuteUpdate method ", new Integer(i), new Integer(1));

//	  pstmt=executeStatement("delete from clav where sm = 666");
//		try
//		{
//			i=pstmt.executeUpdate();
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable ",se);
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testExecuteUpdate method. b=",new Integer(i),new Integer(1));
	}

	public void testStringExecuteUpdate()
	{
		PreparedStatement pstmt=null;
	boolean b=false;
		int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			i = pstmt.executeUpdate("select * from basic where sm = 110");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testStringExecuteUpdate method. b=",new Integer(i),new Integer(1));

		try
		{
			resultSet=pstmt.getResultSet();  // devuelve null
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testExecuteUpdate: The throwable object should be an SQLException",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecuteUpdate method . Unexpected throwable " +
				ex.getClass());
		}

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			i = pstmt.executeUpdate("select * from basic where int = 20");
			} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex2.getClass());
		}

		pstmt=executeStatement("select * from basic where int = ?");
		try
		{
			i = pstmt.executeUpdate("select * from basic where int = 66");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex2) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex2.getClass());
		}

		pstmt=executeStatement("select * from basi where int = ?");
		try
		{
			i = pstmt.executeUpdate("select * from basi where int = 20");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex3) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex3.getClass());
		}

		pstmt=executeStatement("insert into clave(sm,car)values(303,'yo')");
		try
		{
			i = pstmt.executeUpdate("insert into clave(sm,car)values(333,'yo')");
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable ",se);
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex4.getClass());
		}

//		try
//		{
//		  connection.close();
//			openConnection();
//			pstmt=executeStatement("delete from clave where sm = 666");
//
//			i=pstmt.executeUpdate("delete from clave where sm = 666");
//		} catch (SQLException se) {
//			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " + se.getMessage());
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testStringExecuteUpdate method. b=",new Integer(i),new Integer(1));

		pstmt=executeStatement("insert into clave(sm,car)values(666,'tú')");
		try
		{
			i=pstmt.executeUpdate("insert into clave(sm,car)values(666,'tú')");
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " + se.getMessage());
		}
		catch (Throwable ex4) {
			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
				ex4.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testStringExecuteUpdate method ", new Integer(i), new Integer(1));

//	  pstmt=executeStatement("delete from clav where sm = 666");
//		try
//		{
//			i=pstmt.executeUpdate("delete from clav where sm = 666");
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable ",se);
//		}
//		catch (Throwable ex4) {
//			fail("CtsqlPreparedStatementTest. testStringExecuteUpdate method . Unexpected throwable " +
//     			ex4.getClass());
//		}
//		assertEquals("CtsqlPreparedStatementTest. testStringExecuteUpdate method. b=",new Integer(i),new Integer(1));
	}

	public void testSetObject()
	{
		PreparedStatement pstmt=null;
		boolean b = false;
		GregorianCalendar gc = new GregorianCalendar(14,06,00,16,30,00);
		pstmt=executeStatement("select * from basic where sm = ?");

	try
		{
			pstmt.setObject(1,new Short((short)100));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Short((short)100),java.sql.Types.SMALLINT);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Short((short)100),java.sql.Types.SMALLINT,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new String("100"),java.sql.Types.SMALLINT,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Integer("100"),java.sql.Types.SMALLINT,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Double("100"),java.sql.Types.SMALLINT,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new String("Maria"));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new String("Maria"),java.sql.Types.CHAR);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

	pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new String("Maria"),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new Short((short)100),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new Integer(100),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new Double(100),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new Date(gc.getTime().getTime()),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setObject(1,new Time(gc.getTime().getTime()),java.sql.Types.CHAR,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("SqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject: prepareStatement method. " + th.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where int = ?");
	try
		{
			pstmt.setObject(1,new Integer(100));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Integer(100),java.sql.Types.INTEGER);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Integer(100),java.sql.Types.INTEGER,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new String("100"),java.sql.Types.INTEGER,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Short((short)100),java.sql.Types.INTEGER,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Integer(100),java.sql.Types.INTEGER,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);


		pstmt=executeStatement("select * from basic where dec = ?");
	try
		{
			pstmt.setObject(1,new Double(22.56));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Double(22.56),java.sql.Types.DECIMAL);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Double(22.56),java.sql.Types.DECIMAL,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new String("22.56"),java.sql.Types.DECIMAL,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Short((short)22.56),java.sql.Types.DECIMAL,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setObject(1,new Integer((int)22.56),java.sql.Types.DECIMAL,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetObject. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetObject method "+ th.getClass()+ " message =  " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetObject method. b=" +b,b);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setObject(1,new Date(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDate method. b=" +b,b);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setObject(1,new Date(gc.getTime().getTime()),java.sql.Types.DATE);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDate method. b=" +b,b);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setObject(1,new Date(gc.getTime().getTime()),java.sql.Types.DATE,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDate method. b=" +b,b);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setObject(1,new String("14/06/00"),java.sql.Types.DATE,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDate method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new Time(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method." + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new Time(gc.getTime().getTime()),java.sql.Types.TIME);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new Time(gc.getTime().getTime()),java.sql.Types.TIME,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new String("16/30/00"),java.sql.Types.TIME,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new Time(gc.getTime().getTime()),java.sql.Types.DATE,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setObject(1,new Boolean(false),java.sql.Types.TIME,5);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetTime: prepareStatement method.",th);
		}
	}

	public void testSetNull()
	{
		PreparedStatement pstmt=null;
		boolean b = false;
		String result="ola";
		short sh=-31;
		int in=-31;
		double d=-31;
		GregorianCalendar gc = new GregorianCalendar(01,01,01,01,01,01);
		java.sql.Date dt=new java.sql.Date(gc.getTime().getTime());
		java.sql.Time tm=new java.sql.Time(gc.getTime().getTime());

		pstmt=executeStatement("insert into clave(sm,car)values(?,'lala')");
	try
		{
			pstmt.setNull(1,java.sql.Types.SMALLINT);
			b=pstmt.execute();
			pstmt=executeStatement("select * from clave where sm = ?");
			pstmt.setNull(1,java.sql.Types.SMALLINT);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			sh = resultSet.getShort(1);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);


		pstmt=executeStatement("insert into clave(sm,car)values(7,?)");
	try
		{
			pstmt.setNull(1,java.sql.Types.CHAR);
			b=pstmt.execute();
			pstmt=executeStatement("select * from clave where car = ?");
			pstmt.setNull(1,java.sql.Types.CHAR);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			result = resultSet.getString(2);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);

		pstmt=executeStatement("insert into basic(sm,int)values(8,?)");
	try
		{
			pstmt.setNull(1,java.sql.Types.CHAR);
			b=pstmt.execute();

			pstmt=executeStatement("select * from basic where int = ?");
			pstmt.setNull(1,java.sql.Types.INTEGER);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			in = resultSet.getInt(3);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);

		pstmt=executeStatement("select * from basic where dec = ?");
		try
		{
			pstmt.setNull(1,java.sql.Types.DECIMAL);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			d = resultSet.getDouble(4);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);

		pstmt=executeStatement("select * from basic where dt = ?");
		try
		{
			pstmt.setNull(1,java.sql.Types.DATE);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			dt = resultSet.getDate(5);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);

		pstmt=executeStatement("select * from basic where tm = ?");
		try
		{
			pstmt.setNull(1,java.sql.Types.TIME);
			b=pstmt.execute();
			resultSet=pstmt.getResultSet();
			tm = resultSet.getTime(6);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, b);

		pstmt=executeStatement("delete from basic where sm = 8");
	try
		{
			b=pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetNull. SQLException with message "+ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetNull method. b=" +b, (!b));
	}

	public void testSetShort()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setShort(1,(short)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetShort. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetShort method. b=" +b,b);

	  pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setShort(2,(short)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetShort. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetShort: prepareStatement method. ",th);
		}
	}

	public void testSetInt()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from basic where int = ?");
	try
		{
			pstmt.setInt(1,20);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetInt. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetInt: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

	  pstmt=executeStatement("select * from basic where int = ?");
	try
		{
			pstmt.setInt(2,100);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetInt. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetInt: prepareStatement method. ",th);
		}
	}

	public void testSetDouble()
	{
		PreparedStatement pstmt=null;
		boolean b = false;
		double db = 0.0;

		//pstmt=executeStatement("insert into basic(sm)values(200,'ella')");
		pstmt=executeStatement("select * from basic");
		try
		{
			b = pstmt.execute();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testExecute method . Unexpected throwable " +
				ex.getClass());
		}
		assertTrue("CtsqlPreparedStatementTest. testExecute method. b=" +b, b);


		pstmt=executeStatement("select * from basic where dec = ?");
		ResultSet rs = null;
	try
		{
			pstmt.setDouble(1,(double)22.56);
			b = pstmt.execute();
			rs = pstmt.getResultSet();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method." + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDouble method. The resultSet shouldn't be null",b);
		assertNotNull("CtsqlPreparedStatementTest. testSetDouble method. The resultSet shouldn't be null", rs);

		try{
			boolean bool = rs.next();
			if (bool)
				db = rs.getDouble(4);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. " + th.getMessage());
		}

		assertEquals("CtsqlPreparedStatementTest.testSetDouble: The retrieved value isn't correct",new Double(22.56), new Double(db));

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setInt(2,1000);
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. ",th);
		}
	}

	public void testSetString()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setString(1,"Maria");
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

	  pstmt=executeStatement("select * from basic where ch = ?");
	try
		{
			pstmt.setString(2,"Ana");
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDouble. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetDouble: prepareStatement method. ",th);
		}
	}

	public void testSetTime()
	{
		PreparedStatement pstmt=null;
		boolean b = false;
		GregorianCalendar gc = new GregorianCalendar(14,06,00,16,30);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setTime(1,new Time(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetInt method. b=" +b,b);

		pstmt=executeStatement("select * from basic where tm = ?");
	try
		{
			pstmt.setTime(1,new Time(gc.getTime().getTime()));  // ERROR
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from bas where tm = ?");	// ERROR
	try
		{
			pstmt.setTime(1,new Time(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from bas where tm = ?");
	try
		{
			pstmt.setTime(3,new Time(gc.getTime().getTime()));  // ERROR
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest.testSetTime: prepareStatement method. " + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetTime. SQLException with message ",th);
		}
	}

	public void testSetDate()
	{
		PreparedStatement pstmt=null;
		boolean b = false;
		GregorianCalendar gc = new GregorianCalendar(10,01,00,16,30);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setDate(1,new Date(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message " + ex.getMessage());
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}
		assertTrue("CtsqlPreparedStatementTest. testSetDate method. b=" +b,b);

		pstmt=executeStatement("select * from basic where dt = ?");
	try
		{
			pstmt.setDate(1,new Date(gc.getTime().getTime()));  // ERROR
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from bas where dt = ?");	// ERROR
	try
		{
			pstmt.setDate(1,new Date(gc.getTime().getTime()));
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetDate: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from bas where dt = ?");
	try
		{
		  pstmt.setDate(3,new Date(gc.getTime().getTime()));  // ERROR
			b = pstmt.execute();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest.testSetDate: prepareStatement method." + ex.getMessage());
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetDate. SQLException with message ",th);
		}
	}

	public void testSetBoolean()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setBoolean(1,true);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetBoolean. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetBoolean: prepareStatement method. " + th.getMessage());
		}

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setBoolean(1,false);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetBoolean. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetBoolean: prepareStatement method. " + th.getMessage());
		}

	  pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setBoolean(2,true);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetBoolean. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetBoolean: prepareStatement method.",th);
		}
	}

	public void testSetByte()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setByte(1,(byte)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetByte. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetByte: prepareStatement method.  " + th.getMessage());
		}

	  pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setByte(2,(byte)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetByte. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetByte: prepareStatement method.",th);
		}
	}

	public void testSetLong()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setLong(1,(long)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetLong. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetLong: prepareStatement method. " + th.getMessage());
		}

	  pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setLong(2,(long)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetLong. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetLong: prepareStatement method. ",th);
		}
	}

	public void testSetFloat()
	{
		PreparedStatement pstmt=null;
		boolean b = false;

		pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setFloat(1,(float)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetFloat. SQLException with message ",ex);
		}catch (Throwable th)
		{
			fail("CtsqlPreparedStatementTest.testSetFloat: prepareStatement method. " + th.getMessage());
		}

	  pstmt=executeStatement("select * from clave where sm = ?");
	try
		{
			pstmt.setFloat(2,(float)100);
			b = pstmt.execute();
		}catch(SQLException ex){
			assertNotNull("CtsqlPreparedStatementTest. Method: testSetFloat. SQLException with message ",ex);
		}catch (Throwable th)
		{
			assertNotNull("CtsqlPreparedStatementTest.testSetFloat: prepareStatement method.",th);
		}
	}

	public void testGetMaxFieldSize()
	{
		PreparedStatement pstmt=null;
		int i=0;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			i=pstmt.getMaxFieldSize();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. testGetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlPreparedStatementTest. testGetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlPreparedStatementTest. testGetMaxFieldSize method. MaxFieldSize must be equal  32767 and is equal to " +i,i== 32767);
	}

	public void testSetMaxFieldSize()
	{
		PreparedStatement pstmt=null;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setMaxFieldSize(2000);
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. testSetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlPreparedStatementTest. testSetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}
//		assertTrue("CtsqlPreparedStatementTest. testSetMaxFieldSize method. MaxFieldSize must be equal  32767 and is equal to " +i,i== 32767);
	}

	public void testGetMaxRows()
	{
		PreparedStatement pstmt=null;
		int i=0;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			i=pstmt.getMaxRows();
	}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. testGetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlPreparedStatementTest. testGetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlPreparedStatementTest testGetMaxRows: MaxRows must be >=0 and i= " +i ,i==0);
	}

	public void testSetMaxRows()
	{
		PreparedStatement pstmt=null;
		int i=0;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setMaxRows(2000);
			i=pstmt.getMaxRows();
		}catch(SQLException ex){
			fail("CtsqlPreparedStatementTest. testSetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlPreparedStatementTest. testSetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlPreparedStatementTest. testSetMaxRows: i=" +i,i==0);
	}

	public void testSetEscapeProcessing()
	{
		PreparedStatement pstmt=null;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setEscapeProcessing(true);
		}catch(SQLException ex){
			assertTrue("CtsqlPreparedStatementTest. testSetEscapeProcessing: The throwable object should be an SQLException", ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testSetEscapeProcessing. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setEscapeProcessing(false);
		}catch(SQLException ex){
			assertTrue("CtsqlPreparedStatementTest. testSetEscapeProcessing: The throwable object should be an SQLException", ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testSetEscapeProcessing. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testGetWarnings()
	{
		PreparedStatement pstmt=null;
		SQLWarning sQLWarning;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			sQLWarning=pstmt.getWarnings();
			}catch(SQLException ex){
				assertTrue("CtsqlPreparedStatementTest testGetWarnings method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
		}catch(Throwable th){
			assertNotNull("CtsqlPreparedStatementTest. Method: testGetWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message ",th);
		}
	}

	public void testClearWarnings()
	{
		PreparedStatement pstmt=null;
	  pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.clearWarnings();
		}catch(SQLException ex){
				assertNotNull("CtsqlPreparedStatementTest testClearWarnings method: The throwable object should be an SQLException" + ex.toString(),ex);
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testClearWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

	}

	public void testSetCursorName()
	{
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		boolean b=false;
		String Nombre=" ";

		pstmt=executeStatement("select * from clave");
		try
		{
			b=pstmt.execute();
			rs=pstmt.getResultSet();
			pstmt.setCursorName("nulo");
			Nombre=rs.getCursorName();
		}catch(SQLException ex){
		  fail("CtsqlPreparedStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown ");
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlPreparedStatementTest. Method: testSetCursorName.",new String("nulo"),new String(Nombre));

		pstmt=executeStatement("select * from clave");
		try
		{
			b=pstmt.execute();
			rs=pstmt.getResultSet();
//			Nombre=rs.getCursorName();
			pstmt.setCursorName("null");
			Nombre=rs.getCursorName();
		}catch(SQLException ex){
		  fail("CtsqlPreparedStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown ");
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlPreparedStatementTest. Method: testSetCursorName.",new String("null"),new String(Nombre));
	}

	public void testGetMoreResults()
	{
		PreparedStatement pstmt=null;
		boolean b=false;
		String Nombre;

		pstmt=executeStatement("select * from clave where sm = ?");
		try
		{
			pstmt.setShort(1,(short)100);
			b=pstmt.execute();
			b=pstmt.getMoreResults();
		}catch(SQLException ex){
		  assertNotNull("CtsqlPreparedStatementTest. Method: testGetMoreResults. A Throwable shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlPreparedStatementTest. Method: testGetMoreResults. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testGetResultSet()
	{
		PreparedStatement pstmt=null;
	boolean b=false;
		int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)100);
			b = pstmt.execute();
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testGetResultSet method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testGetResultSet method . Unexpected throwable " +
				ex.getClass());
		}
		assertNotNull("CtsqlPreparedStatementTest. testGetResultSet method. b=",resultSet);

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)100);
			i = pstmt.executeUpdate();
			resultSet=pstmt.getResultSet();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testGetResultSet method . Unexpected throwable ",se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testGetResultSet method . Unexpected throwable " +
				ex.getClass());
		}
		assertNull("CtsqlPreparedStatementTest. testGetResultSet method. b=",resultSet);
	}

	public void testGetUpdateCount()
	{
		PreparedStatement pstmt=null;
	boolean b=false;
		int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)110);
		  i = pstmt.executeUpdate();
			i=pstmt.getUpdateCount();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testGetUpdateCount method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testGetUpdateCount method . Unexpected throwable " +
				ex.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testGetUpdateCount method. b=",new Integer(i),new Integer(1));

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)110);
		  b = pstmt.execute();
			i=pstmt.getUpdateCount();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testGetUpdateCount method . Unexpected throwable ",se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testGetUpdateCount method . Unexpected throwable " +
				ex.getClass());
		}
		assertEquals("CtsqlPreparedStatementTest. testGetUpdateCount method. b=",new Integer(i),new Integer(-1));
	}

	public void testClose()
	{
		PreparedStatement pstmt=null;
	int i=0;

		pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.setShort(1,(short)110);
		  i = pstmt.executeUpdate();
			i=pstmt.getUpdateCount();
			pstmt.close();
		} catch (SQLException se) {
			fail("CtsqlPreparedStatementTest. testClose method . Unexpected throwable " +
				se.getMessage());
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testClose method . Unexpected throwable " +
				ex.getClass());
		}

	  pstmt=executeStatement("select * from basic where sm = ?");
		try
		{
			pstmt.close();
		} catch (SQLException se) {
			assertNotNull("CtsqlPreparedStatementTest. testClose method . Unexpected throwable ",se);
		}
		catch (Throwable ex) {
			fail("CtsqlPreparedStatementTest. testClose method . Unexpected throwable " +
				ex.getClass());
		}
	}

//	public void testSetBigDecimal()
//	{
//		int i=0;
//		String descripcion;
//		double prcost;
//		BigDecimal bigdecimal;
//		ResultSet resultSet=null;
//
//		System.out.println("testSetBigDecimal");
//
//		try
//		{
//			pstmt = connection.prepareStatement("select * from articulos where pr_cost >?");
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetBigDecimal: prepareStatement method. Unexpected throwable");
//		}
//
//		try
//		{
//			bigdecimal= new BigDecimal(42000);
//			pstmt.setBigDecimal(1,bigdecimal);
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetBigDecimal: setBigDecimal method. Unexpected throwable");
//		}
//
//		try
//		{
//			resultSet=pstmt.executeQuery();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetBigDecimal: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("SqlPreparedStatementTest. testSetBigDecimal method. The resultset could not be null",resultSet);
//
//		try
//		{
//	  	while (resultSet.next())
//	  	{
//				descripcion = resultSet.getString("descripcion");
//				prcost = resultSet.getDouble("pr_cost");
//				System.out.println("descripcion ="+descripcion + " " + prcost);
//	  	}
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetBigDecimal: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//	}

//	public void testSetFloat()
//	{
//		int i=0;
//		String descripcion;
//		double prcost;
//		ResultSet resultSet=null;
//
//		System.out.println("testSetFloat");
//
//		try
//		{
//			pstmt = connection.prepareStatement("select * from articulos where pr_cost >?");
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetFloat: prepareStatement method. Unexpected throwable");
//		}
//
//		try
//		{
//			pstmt.setFloat(1,40000);
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetFloat: setFloat method. Unexpected throwable");
//		}
//
//		try
//		{
//			resultSet=pstmt.executeQuery();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetFloat: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("SqlPreparedStatementTest. testSetFloat method. The resultset could not be null",resultSet);
//
//		try
//		{
//	  	while (resultSet.next())
//	  	{
//				descripcion = resultSet.getString("descripcion");
//				prcost = resultSet.getDouble("pr_cost");
//				System.out.println("descripcion ="+descripcion + " " + prcost);
//	  	}
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetFloat: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//	}

//	public void testSetLong()
//	{
//		int i=0;
//		String descripcion;
//		double prcost;
//		ResultSet resultSet=null;
//
//		try
//		{
//			pstmt = connection.prepareStatement("select * from articulos where pr_vent >?");
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetLong: prepareStatement method. Unexpected throwable");
//		}
//
//		try
//		{
//			pstmt.setLong(1,40000);
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testSetLong: setLong method. Unexpected throwable");
//		}
//
//		try
//		{
//			resultSet=pstmt.executeQuery();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetLong: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("SqlPreparedStatementTest. testSetLong method. The resultset could not be null",resultSet);
//
//		try
//		{
//	  	while (resultSet.next())
//	  	{
//				descripcion = resultSet.getString("descripcion");
//				prcost = resultSet.getDouble("pr_vent");
//				System.out.println("descripcion ="+descripcion + " " + prcost);
//	  	}
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testSetLong: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//	}


//	public void testClearParameters()
//	{
//		int i=0;
//		String descripcion;
//		String apellidos;
//		ResultSet resultSet=null;
//
//		System.out.println("testClearParameters");
//
//		try
//		{
//			pstmt = connection.prepareStatement("select * from clientes where nombre =?");
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testClearParameters: prepareStatement method. Unexpected throwable");
//		}
//
//		try
//		{
//			pstmt.setString(1,"ANA");
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("SqlPreparedStatementTest.testClearParameters: setString method. Unexpected throwable");
//		}
//
//		try
//		{
//			resultSet=pstmt.executeQuery();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testClearParameters: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("SqlPreparedStatementTest. testClearParameters method. The resultset could not be null",resultSet);
//
//		try
//		{
//	  	while (resultSet.next())
//	  	{
//				apellidos = resultSet.getString("apellidos");
//				System.out.println("apellidos ="+apellidos);
//	  	}
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testClearParameters: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//
//		try
//		{
//			pstmt.clearParameters();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testClearParameters: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//
//		try
//		{
//			resultSet=pstmt.executeQuery();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testClearParameters: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("SqlPreparedStatementTest. testClearParameters method. The resultset could not be null",resultSet);
//
//		try
//		{
//	  	while (resultSet.next())
//	  	{
//				apellidos = resultSet.getString("apellidos");
//				System.out.println("apellidos ="+apellidos);
//	  	}
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("SqlPreparedStatementTest. testClearParameters: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//
//	}


	private Class registerDriver()
	{
		java.lang.Class obj = null;
		try
		{
			obj = Class.forName(driver);
		}catch(ClassNotFoundException ex)
		{
			System.out.println(ex.getMessage());
			fail("SqlPreparedStatementTest. RegisterDriver method. Driver not found ");
		}
		return obj;
	}

	private void openConnection()
	{
		try
		{
			String url = createUrl(host, protocol, port, dbpath, null);
			connection = DriverManager.getConnection(url, user, password);
			assertNotNull("SqlPreparedStatementTest. Private method openConnection. The connection shouldn't be null", connection);
		}catch(Throwable ex)
		{
			fail("SqlPreparedStatementTest. OpenConnection private method. Unexpected Exception" +url);
		}
	}

	private String createUrl(String host, String protocol, int port, String path, String name)
	{
		// Ej. "jdbc:ctsql://mother:20000/insecuss;DBPATH=/disk3/caravel/demos/insecuss/bd"
		String url = new String("jdbc:"+protocol+"://" + host +":" + port);
		if(name != null){
			url = url.concat("/"+ name);
		}
		if(path != null){
			url = url.concat(";DBPATH="+ path);
		}
		return url;
	}

}
