package net.aonsolutions.api.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.api.test.http.APIServletTestSuite;

@Suite
@SelectClasses({
	APIServletTestSuite.class
})
public class APITestSuite {

	
}
