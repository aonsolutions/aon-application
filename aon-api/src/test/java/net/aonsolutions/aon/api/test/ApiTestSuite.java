package net.aonsolutions.aon.api.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.aon.api.test.login.LoginTest;
import net.aonsolutions.aon.api.test.rawdoc.RawdocTest;

@Suite
@SelectClasses({
	LoginTest.class, RawdocTest.class
})
public class ApiTestSuite {

	
}
