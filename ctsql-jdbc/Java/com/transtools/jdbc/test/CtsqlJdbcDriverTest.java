package com.transtools.jdbc.test;

import com.transtools.jdbc.CtsqlJdbcDriver;

import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;


public class CtsqlJdbcDriverTest extends TestCase {
	private CtsqlJdbcDriver driver;
	private String urlValid;
	private String urlNoValid;
	private String hostPort;
	private String dbProps;

	private java.util.Properties props;

	public  CtsqlJdbcDriverTest(String name){
		super(name);
		try{
			driver = new CtsqlJdbcDriver();
		}catch( SQLException ex){
			fail("CtsqlJdbcDriverTest. Constructor. The driver can't be created");
		}
	}

	public void setUp(){
		urlValid = "jdbc:ctsql://turing:11000;C:\\Databases;JDBCTest";
		urlNoValid = "jdbc:multibase://turing:11000;C:\\Databases;JDBCTest";
		props = new Properties();
		props.setProperty("DBUSER","ctl");
		props.setProperty("DBPASSWORD","tornasol");
		hostPort = urlValid.substring(urlValid.indexOf("//")+2, urlValid.indexOf(";",urlValid.indexOf("//")));
		System.out.println("Valor de hostPort " + hostPort);
		dbProps = urlValid.substring(urlValid.indexOf(";")+1,urlValid.length());
		System.out.println("Valor de dbProps " + dbProps);

	}

	public void testAcceptsURL(){
		assertNotNull("CtsqlJdbcDriver. Method: testAcceptsURL. The driver shouldn't be null", driver);
		boolean result;
		try{
		  result = driver.acceptsURL(urlValid);
			assertTrue("CtsqlJdbcDriver. Method: testAcceptsURL. The result should be true, it's a valid URL ", result);
		}catch(SQLException e){
			fail("CtsqlJdbcDriverTest. Method: testAcceptsURL. An SQLException has been thrown  with message " + e.getMessage());
		}

		try{
			result = driver.acceptsURL(urlNoValid);
			assertTrue("CtsqlJdbcDriver. Method: testAcceptsURL. The result should be true, it ins't a valid URL ", (!result));
		}catch(SQLException e){
			fail("CtsqlJdbcDriverTest. Method: testAcceptsURL. An SQLException has been thrown  with message " + e.getMessage());
		}
	}

	/**
	 * jdbc.pending. Queda pendiente testear la excepción SQLException an caso que
	 * el url sea válido pero no se pueda establecer la conexión con la base de datos.
	 * Por ahora no se ha incluido por no tener implementada la clase Connection.
	 */

//	public void testConnect(){
//		assertNotNull("CtsqlJdbcDriver. Method: testConnect. The driver shouldn't be null", driver);
//
//		Connection connection ;
//		try{
//			System.out.println("CtsqlJdbcDriver. Method: testConnect. Antes de connect");
//			connection = driver.connect(urlValid,props);
//			System.out.println("CtsqlJdbcDriver. Method: testConnect. Después de connect");
//			assertNotNull("CtsqlJdbcDriver. Method: testConnect. The connection shouldn't be null", connection);
//		}catch(SQLException ex){
//			fail("CtsqlJdbcDriverTest. Method: testConnect. An SQLException has been thrown  with message " + ex.getMessage());
//		}
//
//		assertEquals("CtsqlJdbcDriverTest. Method: testConnect. The host is not correct ", getHost(),"turing");
//		assertEquals("CtsqlJdbcDriverTest. Method: testConnect. The port is not correct ", new Integer(getPort()),new Integer(11000));
//		assertEquals("CtsqlJdbcDriverTest. Method: testConnect. The dbPath is not correct ", getDBPath(),"C:\\Databases");
//		assertEquals("CtsqlJdbcDriverTest. Method: testConnect. The dbName is not correct ", getDBName(),"test");
//
//		try{
//			connection = driver.connect(urlNoValid,props);
//			assertNull("CtsqlJdbcDriver. Method: testConnect. The connection shouldn't be null", connection);
//		}catch(SQLException ex){
//			fail("CtsqlJdbcDriverTest. Method: testConnect. An SQLException has been thrown  with message " + ex.getMessage());
//		}
//
//	}

	private String getHost(){
		return hostPort.substring(0, hostPort.indexOf(":"));
	}

	private int getPort(){
		return Integer.parseInt(hostPort.substring(hostPort.indexOf(":")+1, hostPort.length()));
	}

	private String getDBPath(){
		return dbProps.substring(0, dbProps.indexOf(";"));
	}

	private String getDBName(){
		return dbProps.substring(dbProps.indexOf(";")+1, dbProps.length());
	}

}
