package net.aonsolutions.occam.test.config;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 ScopeTest.class
	,GeoZoneTest.class
	,BookingTest.class
	,UserTest.class
	,DomainTest.class
})
public class ConfigTestSuite {

	
}
