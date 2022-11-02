package es.aonsolutions.aio.test.invoice;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses({
	InvoiceInsertTest.class,
	InvoiceListTest.class,
	InvoiceRecordTest.class,
})
@Suite
public class InvoiceTestSuite {
	
}
