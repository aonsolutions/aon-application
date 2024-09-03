package net.aonsolutions.aon.api.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.aon.api.test.login.LoginTest;

@Suite
@SelectClasses({
	LoginTest.class
})
public class ApiTestSuite {

	
}
