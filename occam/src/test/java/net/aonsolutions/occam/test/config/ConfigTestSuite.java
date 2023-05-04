package net.aonsolutions.occam.test.config;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	DomainTest.class
	,UserTest.class
})
public class ConfigTestSuite {

	
}
