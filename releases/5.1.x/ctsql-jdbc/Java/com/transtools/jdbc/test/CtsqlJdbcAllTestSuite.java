package com.transtools.jdbc.test;

import java.sql.Connection;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the sample tests
 *
 */
public class CtsqlJdbcAllTestSuite {

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
		
	public static Test suite ( ) {
		
		TestSuite suite= new TestSuite("All JUnit Tests");
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlJdbcDriverTest.class));
//		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlCreateDataTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlConnectionTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlConnectionTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlPreparedStatementTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlResultSetMetaDataTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlResultSetTest.class));
		suite.addTest(new TestSuite(com.transtools.jdbc.test.CtsqlStatementTest.class));
    return suite;
	}

}
