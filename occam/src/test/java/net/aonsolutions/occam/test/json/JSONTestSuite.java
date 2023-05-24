package net.aonsolutions.occam.test.json;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AonJSONUtilsTest.class
	,ApplicationParameterJSONTest.class
	,AuditJSONTest.class
	,GeozoneJSONTest.class
	,AccountJSONTest.class
	,ActivityJSONTest.class
	,DomainAuditJSONTest.class
	,ScopeJSONTest.class
	,BookingJSONTest.class
	,UserJSONTest.class
	,RegistryJSONTest.class
	,RegistryAddressJSONTest.class
	,DomainJSONTest.class
})
public class JSONTestSuite {

	
}
