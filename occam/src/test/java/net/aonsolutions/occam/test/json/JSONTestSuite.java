package net.aonsolutions.occam.test.json;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AonJSONUtilsTest.class
	,AuditJSONTest.class
	,GeozoneJSONTest.class
	,AccountJSONTest.class
	,DomainAuditJSONTest.class
	,ScopeJSONTest.class
	,BookingJSONTest.class
	,UserJSONTest.class
	,RegistryJSONTest.class
	,DomainJSONTest.class
})
public class JSONTestSuite {

	
}
