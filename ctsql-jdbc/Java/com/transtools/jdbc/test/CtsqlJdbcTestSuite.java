package com.transtools.jdbc.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CtsqlJdbcTestSuite extends TestCase {

	public CtsqlJdbcTestSuite(String name) {
		super (name);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();

//		suite.addTest (new CtsqlConnectionTest("testOpenConnection"));
		suite.addTest (new CtsqlPreparedStatementTest("testExecute"));
//		suite.addTest (new CtsqlPreparedStatementTest("testStringExecute"));
//		suite.addTest (new CtsqlStatementTest("testSetCursorName"));
		return suite;
	}
}
