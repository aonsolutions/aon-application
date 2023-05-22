package net.aonsolutions.watson.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.watson.test.client.ClientTestSuite;
import net.aonsolutions.watson.test.client.util.ClientUtilsTestSuite;
import net.aonsolutions.watson.test.server.ServerTestSuite;

@Suite
@SelectClasses({
	 ClientTestSuite.class
	 ,ClientUtilsTestSuite.class
	 ,ServerTestSuite.class
})
public class WatsonTestSuite {

	
}
