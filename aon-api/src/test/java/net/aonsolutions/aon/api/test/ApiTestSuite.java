package net.aonsolutions.aon.api.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.aonsolutions.aon.api.test.login.LoginTest;

@RunWith(Suite.class)
@SuiteClasses({
	LoginTest.class,
})
public class ApiTestSuite {

	
}
