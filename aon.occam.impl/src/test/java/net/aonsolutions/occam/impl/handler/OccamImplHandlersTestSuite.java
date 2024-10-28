package net.aonsolutions.occam.impl.handler;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("OCCAM IMPL HANDLER TEST SUITE")
@SelectClasses({
	CompanyHandlerTest.class,
	CreditorHandlerTest.class,
	CustomerHandlerTest.class,
	SupplierHandlerTest.class,
	InsertRandomInvoicesTest.class
})

public class OccamImplHandlersTestSuite {

	
	
}
