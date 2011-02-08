package com.transtools.jdbc.test;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import junit.framework.TestCase;



public class CtsqlConnectionTest extends TestCase
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
	private Connection connection1, connection2;

	public CtsqlConnectionTest(String name)
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
		url = createUrl(host,protocol,dbpath,dbname);
	}

//		private void createTable(){
//		PreparedStatement pstmt=null;
//		boolean b=false;
//
//		try
//			{
//				pstmt=executeStatement("create table basic(sm Smallint not null label 'Smallint',ch char(20) label 'char',int Integer label 'integer',dec Decimal label 'decimal',dt Date label 'date',tm Time label 'time')primary key(sm)");
//				b = pstmt.execute();
//			} catch (SQLException se) {
//				fail("CtsqlPreparedStatementTest. createTable method . Unexpected throwable " +
//     			se.getMessage());
//			}
//			catch (Throwable ex) {
//			fail("CtsqlPreparedStatementTest. createTable method . Unexpected throwable " +
//     			ex.getClass());
//			}
//	}
//
//	private void dropTable(){
//		PreparedStatement pstmt=null;
//		boolean b=false;
//
//		pstmt=executeStatement("drop table basic");
//		try
//			{
//				b=pstmt.execute();
//			} catch (SQLException se) {
//				fail("1.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
//     			se.getMessage());
//			}
//			catch (Throwable ex) {
//			fail("2.CtsqlPreparedStatementTest. dropTable method . Unexpected throwable " +
//     			ex.getMessage());
//			}
//	}
//
//	public PreparedStatement executeStatement(String cad){
//
//  	PreparedStatement pstmt=null;
//
//	  try {
//			pstmt = connection.prepareStatement(cad);
//		} catch (SQLException se) {
//			assertNotNull("CtsqlPreparedStatementTest. createStatement method . SQLException ",se);
//		}
//		catch (Throwable ex) {
//			fail("CtsqlPreparedStatementTest. createStatement method . Unexpected throwable " +
//     			ex.getMessage());
//		}
//		return pstmt;
//	}

	protected void tearDown()
	{
		try
		{
//			dropTable();
			connection.close();
		}catch(Throwable ex)
		{
			System.out.println("tearDown: "+ex.getMessage());
		}
	}

	private String createUrl(String host, String protocol, String path, String name)
	{
		return new String("jdbc:"+protocol+"://" + host +":" + port +";" + path + ";" + name);
	}

	private Class registerDriver()
	{
		java.lang.Class obj = null;
		try
		{
			obj = Class.forName(driver);
		}catch(ClassNotFoundException ex)
		{
			fail("CtsqlConnectionTest. RegisterDriver method. Driver not found ");
		}
		return obj;
	}

	public void testConnections(){
		registerDriver();
		connection1=null;
		connection2=null;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		ResultSet rs1, rs2;

		try
		{
			String url = createUrl("turing","ctsql","C:\\Databases","general");
			connection1 = DriverManager.getConnection(url, "ctl", "tornasol");
			pstmt1 = connection1.prepareStatement("select * from empresa");
			rs1 = pstmt1.executeQuery();
			assertNotNull("The resultSet shouldn't be null", rs1);
		}catch(SQLException ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
		}

		try
		{
			String url = createUrl("turing","ctsql","C:\\Databases","Empre001");
			connection2 = DriverManager.getConnection(url, "ctl", "tornasol");
			pstmt2 = connection2.prepareStatement("select * from empresa");
			rs2 = pstmt2.executeQuery();
			assertNotNull("The resultSet shouldn't be null", rs2);
		}catch(SQLException ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
		}

		try{
			pstmt1 = connection1.prepareStatement("select * from grupo");
			rs1 = pstmt1.executeQuery();
			assertNotNull("The resultSet shouldn't be null", rs1);
		}catch(SQLException ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
		}

		try{
			pstmt2 = connection2.prepareStatement("select * from empresa");
			rs2 = pstmt2.executeQuery();
			assertNotNull("The resultSet shouldn't be null", rs2);
		}catch(SQLException ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
		}

//		try{
//			connection1.close();
//		}catch(SQLException ex){
//			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
//		}catch(Throwable ex){
//			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
//		}

//		try{
//			assertTrue("The conneciton1 should be closed ", connection1.isClosed());
//			assertTrue("The connection2 shouldn't be closed", (!connection2.isClosed()));
//		}catch(SQLException ex){
//			fail("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null");
//		}catch(Throwable ex){
//			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
//		}

	}


	public void testOpenConnection()
	{
		registerDriver();

		try
		{
			String url = createUrl(host,"noprotocol",dbpath,dbname);
			connection = DriverManager.getConnection(url, user, password);
			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
		}catch(SQLException ex){
			assertNotNull("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null", ex);
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
			assertTrue("CtsqlConnectionTest. testOpenConnection method: The throwable object should be an SQLException", ex instanceof SQLException);
			assertTrue("The throwable object should be an MultibaseException", ex instanceof UnknownHostException);
			assertNull(connection);
		}

		try{
			String url = createUrl("noHost",protocol,dbpath,dbname);
			connection = DriverManager.getConnection(url, user, password);
			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
		}catch(SQLException ex){
			assertNotNull("CtsqlConnectionTest. Method: testOpenConnection. SQLException shouldn't be null", ex);
		}catch(Throwable ex){
			fail("CtsqlConnectionTest. Method: testOpenConnection. Inexpected exception " + ex.getClass() + " with message " + ex.getMessage());
		}

//		try
//		{
//			String url = createUrl(host,protocol,"C:\\tmp",dbname);
//			connection = DriverManager.getConnection(url, user, password);
//			assertNotNull(connection);
//			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlConnectionTest. testOpenConnection method: The throwable object should be an SQLException", ex instanceof SQLException);
//			assertNull(connection);
//		}
//
//		try
//		{
//			String url = createUrl(host,protocol,dbpath,"Ejemplo");
//			connection = DriverManager.getConnection(url, user, password);
//			assertNotNull(connection);
//			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlConnectionTest. testOpenConnection method: The throwable object should be an SQLException", ex instanceof SQLException);
//			assertNull(connection);
//		}
//
//		try
//		{
//			String url = createUrl(host,protocol,dbpath,dbname);
//			connection = DriverManager.getConnection(url, "user", password);
//			assertNotNull(connection);
//			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlConnectionTest. testOpenConnection method: The throwable object should be an SQLException", ex instanceof SQLException);
//			assertNull(connection);
//		}
//
//		try
//		{
//			String url = createUrl(host,protocol,dbpath,dbname);
//			connection = DriverManager.getConnection(url, user, "password");
//			assertNotNull(connection);
//			fail("CtsqlConnectionTest. testOpenConnection method: A SQLException should occur");
//		}catch(Throwable ex)
//		{
//			System.out.println(ex.getMessage());
//			assertTrue("CtsqlConnectionTest. testOpenConnection method: The throwable object should be an SQLException", ex instanceof SQLException);
//			assertNull(connection);
//		}
//
//		openConnection();

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
			fail("CtsqlConnectionTest. OpenConnection private method. SQLException" + ex.getMessage());
		}catch(Throwable th){
			fail("CtsqlConnectionTest. OpenConnection private method. Unexpected Exception" + th.getMessage());

		}
	}

	public void testCloseConnection()
	{
		registerDriver();
		openConnection();

		try
		{
			connection.close();
		}catch(Throwable ex)
		{
			assertTrue("CtsqlConnectionTest. testCloseConnection: The throwable object should be an SQLException", ex instanceof SQLException);
			fail("CtsqlConnectionTest. testCloseConnection method. Unexpected Exception");
		}
	}

	public void testCreateStatement()
	{

		Statement stmt = null;

		registerDriver();
		openConnection();

		try
		{
			stmt = connection.createStatement();
		}catch (Throwable ex)
		{
			System.out.println(ex.getMessage());
			fail("CtsqlConnectionTest. testCreateStatement method . Unexpected throwable");
		}

		assertNotNull("CtsqlConnectionTest.testCreateStatement method: The statement object should not be NULL", stmt);

	}

	public void testgetTransactionIsolation()
	{
		PreparedStatement pstmt1=null;
		ResultSet rs1;
		boolean b=false;
		int i=0;

		registerDriver();
		openConnection();

		try
		{
			i=connection.getTransactionIsolation();
		}catch (SQLException e){
			fail("CtsqlConnectionTest. testgetTransactionIsolation method. Unexpected throwable");
		}catch (Throwable ex)
		{
			fail("CtsqlConnectionTest. testgetTransactionIsolation method. Unexpected throwable");
		}
//       assertEquals("CtsqlConnectionTest. testgetTransactionIsolation method. The throwable object should be an SQLException",new Integer(i),new Integer(connection.TRANSACTION_READ_UNCOMMITTED));
			assertEquals("CtsqlConnectionTest. testgetTransactionIsolation method. The throwable object should be an SQLException",new Integer(i),new Integer(Connection.TRANSACTION_NONE));
	}

	public void testsetTransactionIsolation()
	{

		int i=0;

		registerDriver();
		openConnection();

		try
		{
			connection.setTransactionIsolation(1);
		}catch (SQLException e){
			assertNotNull("CtsqlConnectionTest. testgetTransactionIsolation method. Unexpected throwable");
		}catch (Throwable ex)
		{
			fail("CtsqlConnectionTest. testgetTransactionIsolation method. Unexpected throwable");
		}
  }


		public void testIsClosed()
		{

			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				b=connection.isClosed();
			}catch (Throwable ex)
			{
				System.out.println(ex.getMessage());
				assertTrue("CtsqlConnectionTest. testIsClosed method. The throwable object should be an SQLException", ex instanceof SQLException);
				fail("CtsqlConnectionTest. testIsClosed method. Unexpected throwable");
			}
			assertTrue("CtsqlConnectionTest. testIsClosed method: The connection is not closed",b==false);

			try
			{
				connection.close();
			}catch (Throwable ex)
			{
				System.out.println(ex.getMessage());
				assertTrue("CtsqlConnectionTest. testIsClosed method.The throwable object should be an SQLException", ex instanceof SQLException);
				fail("CtsqlConnectionTest. testIsClosed method . Unexpected throwable");
			}

			try
			{
				b=connection.isClosed();
			}catch (Throwable ex)
			{
				System.out.println(ex.getMessage());
				assertTrue("CtsqlConnectionTest. testIsClosed method. The throwable object should be an SQLException", ex instanceof SQLException);
				fail("CtsqlConnectionTest. testIsClosed method . Unexpected throwable");
			}
			assertTrue("CtsqlConnectionTest. testIsClosed method: The connection is closed",b==true);
		}

		public void testIsReadOnly()
		{

			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				connection.setReadOnly(false);
			}catch (SQLException e){
				assertNotNull("CtsqlConnectionTest. testisReadOnly method: The throwable object should be an SQLException", e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testisReadOnly method. Unexpected throwable");
			}
	}

		public void testSetReadOnly()
		{

			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				connection.setReadOnly(false);
			}catch (SQLException e){
				assertNotNull("CtsqlConnectionTest. testSetReadOnly method. The throwable object should be an SQLException", e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testSetReadOnly method. Unexpected throwable");
			}
		}

//		public void testPrepareCall()
//		{
//
//			boolean b=false;
//
//			registerDriver();
//			openConnection();
//
//			try
//			{
//				connection.prepareCall("select * from clientes");
//			}catch (Throwable ex)
//			{
//				System.out.println(ex.getMessage());
//		 		assertTrue("CtsqlConnectionTest. testprepareCall method: The throwable object should be an SQLException", ex instanceof SQLException);
//			}
//		}

		public void testNativeSQL()
		{
			String NativeSql=null;

			registerDriver();
			openConnection();

			try
			{
//				createTable();
//				System.out.println("1.	Ha creado las tablas");
				NativeSql=connection.nativeSQL("select * from basic");
			}catch (SQLException e){
				assertNotNull("CtsqlConnectionTest. testNativeSql method: The throwable object should be an SQLException",e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testNativeSql method. Unexpected throwable");
			}
//			dropTable();
//			System.out.println("2.	Ha eliminado las tablas");

		}

		public void testPrepareStatement()
		{

			boolean b=false;
			registerDriver();
			PreparedStatement aPreparedStatement=null;
			PreparedStatement aPreparedStatement1=null;
			openConnection();

//			createTable();
//			System.out.println("1.	Ha creado las tablas");
			try
			{
				aPreparedStatement=connection.prepareStatement("select * from basic");
			}catch (SQLException e){
				fail("CtsqlConnectionTest. testPrepareStatement method. Unexpected throwable " + e.getMessage());
			}catch (Throwable ex){
				fail("CtsqlConnectionTest. testPrepareStatement method. Unexpected throwable "+ ex.getMessage() );
			}

			try
			{
				aPreparedStatement=connection.prepareStatement("select * from basic");
			}catch (SQLException e){
				assertNotNull("CtsqlConnectionTest. testPrepareStatement method. Unexpected throwable ",e);
			}catch (Throwable ex){
				fail("CtsqlConnectionTest. testPrepareStatement method. Unexpected throwable "+ ex.getMessage() );
			}
//			dropTable();
//			System.out.println("2.	Ha eliminado las tablas");
		}

		public void testGetMetaData()
		{
			boolean b=false;
			String driver_name="";
			registerDriver();
			DatabaseMetaData aDatabaseMetaData=null;
			openConnection();

			try
			{
				aDatabaseMetaData=connection.getMetaData();
				driver_name=aDatabaseMetaData.getDriverName();
			}catch (SQLException e){
				fail("testGetCatalog method: The throwable object should be an SQLException");
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testGetCatalog method. Unexpected throwable "+ ex.getMessage() );
			}
				assertNotNull("CtsqlConnectionTest.testGetMetaData method:The DatabaseMetaData object should not be NULL", aDatabaseMetaData);
				assertEquals("CtsqlConnectionTest.testGetMetaData method:The DatabaseMetaData object should not be NULL",new String(driver_name),new String("TransTOOLs Ctsql JDBC Driver"));
		}

		public void testGetCatalog()
		{
			String catalog=null;
			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				catalog=connection.getCatalog();
			}catch (SQLException e){
				assertNotNull("testGetCatalog method: The throwable object should be an SQLException",e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testGetCatalog method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testSetCatalog()
		{
			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				connection.setCatalog("catalogo");
			}catch (SQLException e){
				assertNotNull("testSetCatalog method: The throwable object should be an SQLException",e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testSetCatalog method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testGetWarnings()
		{
			SQLWarning sQLWarning;
			registerDriver();
			openConnection();

			try
			{
				sQLWarning=connection.getWarnings();
			}catch (SQLException e){
				assertNotNull("CtsqlConnectionTest testgetWarnings method: The throwable object should be an SQLException",e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. GetWarnings method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testClearWarnings()
		{
			registerDriver();
			openConnection();

			try
			{
				connection.clearWarnings();
			}catch (SQLException e){
			  assertNotNull("CtsqlConnectionTest testclearWarnings method: The throwable object should be an SQLException",e);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. clearWarnings method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testGetAutoCommit()
		{
			boolean autocommit;
			boolean b=false;

			registerDriver();
			openConnection();

			try
			{
				autocommit=connection.getAutoCommit();
			}catch (SQLException se)
			{
				assertNotNull("CtsqlConnectionTest. testGetAutoCommit method: The throwable object should be an SQLException",se);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testGetAutoCommit method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testSetAutoCommit()
		{
			registerDriver();
			openConnection();

			try
			{
				connection.setAutoCommit(false);
					}catch (SQLException se)
			{
				assertNotNull("CtsqlConnectionTest. testGetAutoCommit method: The throwable object should be an SQLException",se);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testSetAutoCommit method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testCommit()
		{
			registerDriver();
			openConnection();

			try
			{
				connection.commit();
		}catch (SQLException se)
			{
				assertNotNull("CtsqlConnectionTest. testGetAutoCommit method: The throwable object should be an SQLException",se);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testCommit method. Unexpected throwable "+ ex.getMessage() );
			}
		}

		public void testRollback()
		{
			registerDriver();
			openConnection();

			try
			{
				connection.rollback();
					}catch (SQLException se)
			{
				assertNotNull("CtsqlConnectionTest. testGetAutoCommit method: The throwable object should be an SQLException",se);
			}catch (Throwable ex)
			{
				fail("CtsqlConnectionTest. testRollback method. Unexpected throwable "+ ex.getMessage() );
			}
		}

}
