package net.aonsolutions.occam.impl.handler;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("OCCAM IMPL HANDLER TEST SUITE")
@SelectClasses({
	AccountHandlerLoadTest.class,
	AppParamHanlderTest.class,
	CompanyHandlerTest.class,
	CreditorHandlerTest.class,
	CustomerHandlerTest.class,
	SupplierHandlerTest.class,
	InsertRandomInvoicesTest.class,
	InvoiceRecorderHandlerTest.class,
})

public class OccamImplHandlersTestSuite {

	
	
}
