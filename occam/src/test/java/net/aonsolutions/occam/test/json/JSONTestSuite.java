package net.aonsolutions.occam.test.json;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AonJSONUtilsTest.class
	,AuditJSONTest.class
	,DomainAuditJSONTest.class
	,ScopeJSONTest.class
	,BookingJSONTest.class
	,UserJSONTest.class
	,DomainJSONTest.class
})
public class JSONTestSuite {

	
}
