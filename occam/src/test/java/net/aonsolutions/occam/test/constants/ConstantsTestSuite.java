package net.aonsolutions.occam.test.constants;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	DomainTypeTest.class
	,AonStatusTest.class
	,DocumentTypeTest.class	
	,InvoiceTypeTest.class
	,SecurityLevelTest.class
})
public class ConstantsTestSuite {

	
}
