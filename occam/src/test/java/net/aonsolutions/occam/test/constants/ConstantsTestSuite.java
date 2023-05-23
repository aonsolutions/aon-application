package net.aonsolutions.occam.test.constants;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	DomainTypeTest.class
	,AonAppTest.class
	,AonModuleTest.class
	,AonStatusTest.class
	,AonLanguageTest.class
	,AdministrationTest.class
	,DocumentTypeTest.class	
	,InvoiceTypeTest.class
	,SecurityLevelTest.class
	,StreetTypeTest.class
})
public class ConstantsTestSuite {

	
}
