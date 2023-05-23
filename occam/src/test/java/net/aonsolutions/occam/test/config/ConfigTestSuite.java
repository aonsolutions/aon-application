package net.aonsolutions.occam.test.config;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ApplicationParameterTest.class
	,ScopeTest.class
	,GeozoneTest.class
	,BookingTest.class
	,UserTest.class
	,RegistryTest.class
	,RegistryAddressTest.class
	,DomainTest.class
})
public class ConfigTestSuite {

	
}
