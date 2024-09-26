package net.aonsolutions.aon.api.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.aonsolutions.aon.api.test.login.LoginTest;
import net.aonsolutions.aon.api.test.rawdoc.RawdocTest;

@RunWith(Suite.class)
@SuiteClasses({
	LoginTest.class, RawdocTest.class
})
public class ApiTestSuite {

	
}
