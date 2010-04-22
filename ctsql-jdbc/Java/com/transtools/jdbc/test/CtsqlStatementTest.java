package com.transtools.jdbc.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import junit.framework.TestCase;


public class CtsqlStatementTest extends TestCase
{
	protected Connection connection;
	protected String driver;
	protected String protocol;
	protected String host;
	protected String dbpath;
	protected String dbname;
	protected String url;
	protected String user;
	protected String password;
	private int port = 20010;

	public CtsqlStatementTest(String name)
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
	}

	protected void tearDown()
	{
		Statement stmt;
		try
		{
			stmt=null;
			stmt.close();
		}catch(Throwable ex)
		{
			System.out.println(ex.getMessage());
		}

		try
		{
			connection.close();
		}catch(Throwable ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	public void testExecute()
	{
		Statement stmt=null;
		boolean b=false;
		String Nombre=null;
		String descripcion=null;
		ResultSet resultSet=null;
		short sm = -1;
//		try
//		{
//				b = stmt.execute("insert into basic (sm,ch,int,dec,dt,tm) VALUES (400,'Maria',20,2.56789,'06/14/2000','4:30:00');");
//		}catch(SQLException ex){
//			fail("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
//		}catch(Throwable th){
//			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
//		}
//		assertTrue("CtsqlStatementTest. testExecute method. b=" +b, (!b));

		try
		{
				stmt = connection.createStatement();
				b = stmt.execute("select * from basic");
		}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertTrue("CtsqlStatementTest. testExecute method. b=" +b, b);

		try{
			resultSet = stmt.getResultSet();
		}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertNotNull("CtsqlStatementTest. Method: testExecute. The resultSet shouldn't be null ", resultSet);

		try{
			resultSet = stmt.getResultSet();
		}catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		try{
		while (resultSet.next())
				sm = resultSet.getShort(1);
	  }catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//		assertEquals("CtsqlStatementTest. Method: testExecute. The retrieved value is not correct " + sm, new Short(sm), new Short((short)100));

 try{
		while (resultSet.next())
				sm = resultSet.getShort(1);
	  }catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecute. A SQLException shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		try
		{
			b = stmt.execute("select * from client");
			fail("CtsqlStatementTest. Method: testExecute. A SQLException should be thrown ");
		}catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecute. A SQLException should be thrown ", ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

	try
		{ // la tabla ya existe
			b = stmt.execute("create table clave(sm smallint not null,car char(20))primary key (sm);");
		}catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecute. A SQLException shouldn`t be thrown ");
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: . A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

	try
		{
			b = stmt.execute("insert into clave(sm,car) values(245,'Girona')");
			b = stmt.execute("insert into clave(sm,car) values(245,'Girona')");
		}catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecute. A SQLException should be thrown ", ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecute. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
	}
	}

	public void testExecuteQuery()
	{
		Statement stmt=null;
		ResultSet resultSet=null;
		try
		{
			stmt = connection.createStatement();
			resultSet=stmt.executeQuery("select * from basic");
	}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A SQLException shouldn't be throw" + ex.getClass());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());		assertNotNull("CtsqlStatementTest. testExecuteQuery method. The resultset could not be null",resultSet);
		}
		assertNotNull("CtsqlStatementTest. Method: testExecuteQuery. ResultSet shouldn't be null ", resultSet);

		try
		{
			resultSet=stmt.executeQuery("selec * from basic");
	}catch(SQLException ex){
			assertNotNull("CtsqlsStatementTest. testExecuteQuery method 2.SQLException should be null",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());		assertNotNull("CtsqlStatementTest. testExecuteQuery method. The resultset could not be null",resultSet);
		}
		resultSet = null;
		boolean b =true;
		try
		{
			resultSet=stmt.executeQuery("insert into clave(sm,car) values(545,'Gerona')");
		}catch(SQLException ex){
			assertNotNull("CtsqlStatementTest. Method: testExecuteQuery. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

	 try
		{
			b=resultSet.next();
		}catch(SQLException ex){
		fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + ex.getClass() + " with message " + ex.getMessage());
		}catch(Throwable th){
			assertNotNull("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		try
		{
			resultSet=stmt.executeQuery("select * from clave");
	}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A SQLException shouldn't be throw" + ex.getClass());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());		assertNotNull("CtsqlStatementTest. testExecuteQuery method. The resultset could not be null",resultSet);
		}
		assertNotNull("CtsqlStatementTest. Method: testExecuteQuery. ResultSet shouldn't be null ", resultSet);

		try
		{
			resultSet = stmt.getResultSet();
		}catch(SQLException ex){
				assertNotNull("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + ex.getClass() + " with message " + ex.getMessage());
		}catch(Throwable th){
		fail("CtsqlStatementTest. Method: testExecuteQuery. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

	}

	public void testExecuteUpdate()
	{
		Statement stmt=null;
		int i=0;
		try
		{
			  stmt = connection.createStatement();
				i = stmt.executeUpdate("insert into clave(sm,car) values(845,'Gerona')");
		}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlStatementTest. testExecuteUpdate : The value isn't correct ", new Integer(i) , new Integer(1));

		try
		{
			i = stmt.executeUpdate("delete from clave where sm=845");
		}catch(SQLException ex){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A SQLException shouldn't be thrown " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlStatementTest. testExecuteUpdate : The value isn't correct ", new Integer(i) , new Integer(1));


		try
		{
			i=stmt.executeUpdate("insert into clave(sm,car) values(745,'Girona')");
		}catch(SQLException ex){
		  assertNotNull("CtsqlStatementTest. Method: testExecuteUpdate. A SQLException should be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
//		assertEquals("CtsqlStatementTest. testExecuteUpdate : The value isn't correct ", new Integer(i) , new Integer(-1));

		try
		{
			i=stmt.executeUpdate("select * from clave");
		}catch(SQLException ex){
		  assertNotNull("CtsqlStatementTest. Method: testExecuteUpdate. A SQLException should be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testExecuteUpdate. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
}

	public void testGetWarnings()
	{
		Statement stmt=null;
		SQLWarning sQLWarning;

		try
		{
			stmt = connection.createStatement();
			sQLWarning=stmt.getWarnings();
			}catch(SQLException ex){
//		  assertNotNull("CtsqlStatementTest. Method: testGetWarnings. A SQLException should be thrown ",ex);
				assertTrue("CtsqlStatementTest testGetWarnings method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testClearWarnings()
	{
		Statement stmt=null;
		try
		{
			stmt = connection.createStatement();
			stmt.clearWarnings();
		}catch(SQLException ex){
//		  assertNotNull("CtsqlStatementTest. Method: testClearWarnings. A SQLException should be thrown ",ex);
				assertTrue("CtsqlStatementTest testClearWarnings method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testClearWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testSetCursorName()
	{
		Statement stmt=null;
		ResultSet rs=null;
		boolean b=false;
		String Nombre=" ";

		try
		{
			stmt = connection.createStatement();
			rs=stmt.executeQuery("select * from basic");;
			rs=stmt.getResultSet();
			stmt.setCursorName("olitas");
			Nombre=rs.getCursorName();
		}catch(SQLException ex){
		  fail("CtsqlStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown ");
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlStatementTest. Method: testSetCursorName.",new String("olitas"),new String(Nombre));

		try
		{
			rs=stmt.executeQuery("select * from basic");;
			rs=stmt.getResultSet();
			stmt.setCursorName("null");
			Nombre=rs.getCursorName();
		}catch(SQLException ex){
		  fail("CtsqlStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown ");
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testSetCursorName. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertEquals("CtsqlStatementTest. Method: testSetCursorName.",new String("null"),new String(Nombre));
	}

//	public void testGetQueryTimeout()
//	{
//		int i=0;
//		try
//		{
//			i=stmt.getQueryTimeout();
//		}catch (SQLException se){
//
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest testGetQueryTimeout method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
//			fail("CtsqlStatementTest. testGetQueryTimeout method. Unexpected throwable "+ ex.getMessage() );
//		}
//		assertTrue("QueryTimeOut must be >=0 and QueryTimeOut = "+i,i>=0);
//	}

//	public void testSetQueryTimeout()
//	{
//		int i=0;
//		try
//		{
//			stmt.setQueryTimeout(2);
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest testSetQueryTimeout method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
//			fail("CtsqlStatementTest. testSetQueryTimeout method. Unexpected throwable "+ ex.getMessage() );
//		}
//		try
//		{
//			i=stmt.getQueryTimeout();
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest testGetQueryTimeout method: The throwable object should be an SQLException" + ex.toString(), ex instanceof SQLException);
//			fail("CtsqlStatementTest. testGetQueryTimeout method. Unexpected throwable "+ ex.getMessage() );
//		}
//		assertTrue("QueryTimeOut must be equal 2",i==2);
//	}

	public void testSetEscapeProcessing()
	{
		Statement stmt=null;
		try
		{
			stmt = connection.createStatement();
			stmt.setEscapeProcessing(true);
		}catch(SQLException ex){
			assertTrue("CtsqlStatementTest. testSetEscapeProcessing: The throwable object should be an SQLException", ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testClearWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		try
		{
			stmt.setEscapeProcessing(false);
		}catch(SQLException ex){
			assertTrue("CtsqlStatementTest. testSetEscapeProcessing: The throwable object should be an SQLException", ex instanceof SQLException);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testClearWarnings. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testGetMaxFieldSize()
	{
		Statement stmt=null;
		int i=0;
		try
		{
			stmt = connection.createStatement();
			i=stmt.getMaxFieldSize();
		}catch(SQLException ex){
			fail("CtsqlStatementTest. testGetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlStatementTest. testGetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlStatementTest. testGetMaxFieldSize method. MaxFieldSize must be equal  32767 and is equal to " +i,i== 32767);
	}

	public void testSetMaxFieldSize()
	{
		Statement stmt=null;
		try
		{
			stmt = connection.createStatement();
			stmt.setMaxFieldSize(2000);
		}catch(SQLException ex){
			fail("CtsqlStatementTest. testSetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlStatementTest. testSetMaxFieldSize method. Unexpected throwable "+ ex.getMessage() );
		}
//		assertTrue("CtsqlStatementTest. testSetMaxFieldSize method. MaxFieldSize must be equal  32767 and is equal to " +i,i== 32767);
	}

	public void testGetMaxRows()
	{
		Statement stmt=null;
		int i=0;
		try
		{
			stmt = connection.createStatement();
			i=stmt.getMaxRows();
	}catch(SQLException ex){
			fail("CtsqlStatementTest. testGetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlStatementTest. testGetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlStatementTest testGetMaxRows: MaxRows must be >=0 and i= " +i ,i==0);
	}

	public void testSetMaxRows()
	{
		Statement stmt=null;
		int i=0;
		try
		{
			stmt = connection.createStatement();
			stmt.setMaxRows(2000);
			i=stmt.getMaxRows();
		}catch(SQLException ex){
			fail("CtsqlStatementTest. testSetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}catch (Throwable ex)
		{
			fail("CtsqlStatementTest. testSetMaxRows method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlStatementTest. testSetMaxRows: i=" +i,i==0);
	}

	public void testGetUpdateCount()
	{
		Statement stmt=null;
		int i=0;
		boolean b=false;

//		try
//		{
//			i=stmt.executeUpdate("delete from clave where sm =432");
//		} catch (SQLException se) {
//			fail("CtsqlStatementTest. testGetUpdateCount method. Unexpected throwable "+ se.getMessage() );
//		}
//		catch (Throwable ex) {
//			fail("CtsqlStatementTest. testGetUpdateCount method. Unexpected throwable "+ ex.getMessage() );
//		}
//		assertEquals("CtsqlStatementTest. testGetUpdateCount method ", new Integer(i), new Integer(1));


		try
		{
			stmt = connection.createStatement();
			i=stmt.executeUpdate("insert into clave(sm,car)values(432,'él')");
			i=stmt.getUpdateCount();
		}catch(SQLException ex){
			fail("CtsqlStatementTest. testGetUpdateCount method. Unexpected throwable "+ ex.getMessage() );
		}catch(Throwable ex)
		{
			fail("CtsqlStatementTest. testGetUpdateCount method. Unexpected throwable "+ ex.getMessage() );
		}
		assertTrue("CtsqlStatementTest. testGetUpdateCount: i=" +i,i==1);
	}

//	public void testClose()
//	{
//		try
//		{
//			stmt.close();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest. testClose: close: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//	}
//
	public void testGetResultSet()
	{
		Statement stmt=null;
		ResultSet rs=null;
		boolean b=false;
		short Nombre=0;

		try
		{
			stmt = connection.createStatement();
			b=stmt.execute("select * from clave");
		}catch(SQLException ex){
		  fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + ex.getClass() + " with message " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertTrue("CtsqlStatementTest. testGetResultSet : The value isn't correct ",b);

		try
		{
			rs=stmt.getResultSet();
		}catch(SQLException ex){
		  fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + ex.getClass() + " with message " + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
		assertNotNull("CtsqlStatementTest. testGetResultSet : The value isn't correct ",rs);

		try
		{
			b=stmt.execute("select * from client");
		}catch(SQLException ex){
		  assertNotNull("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}

		try
		{
			rs=stmt.getResultSet();
		}catch(SQLException ex){
		  assertNotNull("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetResultSet. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}

	public void testGetMoreResults()
	{
		Statement stmt=null;
		ResultSet rs=null;
		boolean b=false;
		String Nombre;

		try
		{
			stmt = connection.createStatement();
			b=stmt.execute("select * from basic");
			b=stmt.getMoreResults();
		}catch(SQLException ex){
		  assertNotNull("CtsqlStatementTest. Method: testGetMoreResults. A Throwable shouldn't be thrown ",ex);
		}catch(Throwable th){
			fail("CtsqlStatementTest. Method: testGetMoreResults. A Throwable shouldn't be thrown " + th.getClass() + " with message " + th.getMessage());
		}
	}
//
//		try
//		{
//			rs=stmt.getResultSet();
//		}catch(Throwable ex)
//		{
//		 		assertTrue("CtsqlStatementTest. testGetMoreResults: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNotNull("CtsqlStatementTest. testGetMoreResults method. The resultset could not be null", rs);

//		try
//		{
//			System.out.println("CtsqlStatementTest. testGetMoreResults");
//	  	while (stmt.getMoreResults())
//	  	{
//				rs=stmt.getResultSet();
//				Nombre = rs.getString("nombre");
//				System.out.println("nombre ="+Nombre);
//				System.out.println("CtsqlStatementTest. testGetMoreResults dentro del while");
//	  	}
//			System.out.println("FIN CtsqlStatementTest. testGetMoreResults");
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlStatementTest. testGetMoreResults: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//	try
//		{
//			b=stmt.execute("select * from client");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlStatementTest. testGetMoreResults: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertTrue("CtsqlStatementTest. testGetMoreResults method. b=" +b, b);
//
//		try
//		{
//			rs=stmt.getResultSet();
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest. testGetMoreResults: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertNull("CtsqlStatementTest. testGetMoreResults method. The resultset could be null", rs);
//
//	}

//	public void testSetCursorName()
//	{
//		ResultSet rs=null;
//		boolean b=false;
//		int i=0;
//		String descripcion;
//		String cursorName;
//		System.out.println("CtsqlStatementTest. testSetCursorName");
//
//		try
//		{
//			i=stmt.executeUpdate("insert into provincias(provincia,descripcion) values(405,'Girona')");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//		}
//
//		try
//		{
//			stmt.setCursorName("actualiza");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//		 	assertTrue("CtsqlStatementTest. testSetCursorName: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//
//		try
//		{
//			b=stmt.execute("select * from provincias where provincia > 400 for update");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest. testSetCursorName: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertTrue("CtsqlStatementTest. testSetCursorName method. b=" +b, b);
//
//		try
//		{
//			b=stmt.execute("update provincias set descripcion='Actualiza' where current of actualiza");
//		}catch(Throwable ex)
//		{
//	 		assertTrue("CtsqlStatementTest. testSetCursorName: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		assertTrue("CtsqlStatementTest. testSetCursorName method. b=" +b, b);
//
//		try
//		{
//			rs=stmt.getResultSet();
//			cursorName=rs.getCursorName();
//			System.out.println("cursorName= "+ cursorName);
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//	 		assertTrue("CtsqlStatementTest. testSetCursorName: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//
//		try
//		{
//			stmt1 = connection.createStatement();
//		}catch (Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			fail("CtsqlStatementTest. createStatement method . Unexpected throwable");
//		}
//
//		try
//		{
// 			while (rs.next())
//	  	{
//  			System.out.println("entro en while");
//  			descripcion = rs.getString("descripcion");
//				System.out.println("descripcion ="+descripcion);
//
//				b=stmt1.execute("update provincias set descripcion='Francia' where current of actualiza");
//				System.out.println("i = "+i);
//				assertTrue("CtsqlStatementTest. testSetCursorName method. i=" +i, i);
//	  		descripcion = rs.getString("descripcion");
//				System.out.println("descripcion ="+descripcion);
//	  	}
// 			System.out.println("Fin CtsqlStatementTest. testSetCursorName");
//	  }catch(Throwable ex)
//	  {
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlStatementTest. testSetCursorName: The throwable object should be an SQLException", ex instanceof SQLException);
//		}
//		System.out.println("Fin CtsqlStatementTest. testSetCursorName");
//	}
//
	private Class registerDriver()
	{
		java.lang.Class obj = null;
		try
		{
			obj = Class.forName(driver);
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
			String url = createUrl(host,protocol,dbpath,dbname);
			connection = DriverManager.getConnection(url, user, password);
			assertNotNull(connection);
		}catch(SQLException ex)
		{
			fail("CtsqlStatementTest. OpenConnection private method. Unexpected SQLException" + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlStatementTest. OpenConnection private method. Unexpected Exception" + th.getMessage());
		}
	}

	private String createUrl(String host, String protocol, String path, String name)
	{
		return new String("jdbc:"+protocol+"://" + host +":" + port + ";" + path + ";" + name);
	}

}
