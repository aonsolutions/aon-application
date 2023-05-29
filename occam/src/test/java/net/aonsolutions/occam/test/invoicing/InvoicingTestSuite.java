package net.aonsolutions.occam.test.invoicing;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	InvoiceTest.class
	,InvoiceBreakdownTest.class
	,InvoiceDetailTest.class
	,InvoiceTaxTest.class
})
public class InvoicingTestSuite {

	
}
