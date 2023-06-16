package net.aonsolutions.occam.test.config;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	ActivityTest.class
	,ConfigurationTest.class
	,ApplicationParameterTest.class
	,BookingTest.class
	,DomainAuditTest.class
	,DomainTest.class
	,GeozoneTest.class
	,RegistryTest.class
	,RegistryAddressTest.class
	,ScopeTest.class
	,UserTest.class
})
public class ConfigTestSuite {

	
}
