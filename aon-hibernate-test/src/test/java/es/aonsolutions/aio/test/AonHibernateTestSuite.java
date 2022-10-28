package es.aonsolutions.aio.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import es.aonsolutions.aio.test.config.ConfigTestSuite;
import es.aonsolutions.aio.test.invoice.InvoiceTestSuite;

@SelectClasses({
	ConfigTestSuite.class,
	InvoiceTestSuite.class
})
@Suite
public class AonHibernateTestSuite {

	
}
