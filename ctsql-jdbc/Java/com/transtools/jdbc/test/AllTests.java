package com.transtools.jdbc.test;

import junit.framework.TestSuite;

public class AllTests {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTest (new TestSuite(DecimalTest.class));
		suite.addTest (new TestSuite(GetPrecisionScaleTest.class));
		suite.addTest (new TestSuite(SetNullTest.class));
		suite.addTest (new TestSuite(EneTest.class));		

		return suite;
	}

	public static void main ( String[] args ) {

		new SetNullTest("test").run();
	}
}

