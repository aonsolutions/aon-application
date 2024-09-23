package com.esferalia.aon.occam.impl.jooq.dao.invoice;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	InvoiceMinJSONTest.class,
	InvoiceCalculatorTest.class,
	InvoiceValidationTests.class,
	InvoiceDaoInitializationTests.class,
	InvoiceDaoGettersTest.class,
	InvoiceDaoSettersTest.class,
})
public class InvoiceDAOTestSuite {
	
}
