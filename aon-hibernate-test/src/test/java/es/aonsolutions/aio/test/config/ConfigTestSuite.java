package es.aonsolutions.aio.test.config;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses({
	AccountTest.class,
	TaxTest.class,
})
@Suite
public class ConfigTestSuite {
	
}
