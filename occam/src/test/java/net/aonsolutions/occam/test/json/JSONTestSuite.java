package net.aonsolutions.occam.test.json;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 AonJSONUtilsTest.class
	 
	 ,AccountJSONTest.class
	 ,AccountingConfigurationJSONTest.class
	 ,ActivityJSONTest.class
	 ,ApplicationParameterJSONTest.class
	 ,AuditJSONTest.class
	 ,BookingJSONTest.class
	 ,ConfigurationJSONTest.class
	 ,DomainJSONTest.class
	 ,DomainAuditJSONTest.class
	 ,GeozoneJSONTest.class
	 ,RegistryJSONTest.class
	 ,RegistryAddressJSONTest.class
	 ,ScopeJSONTest.class
	 ,UserJSONTest.class
})
public class JSONTestSuite {

	
}
