package es.aonsolutions.aio.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import es.aonsolutions.aio.test.config.ConfigTestSuite;
import es.aonsolutions.aio.test.invoice.InvoiceTestSuite;
import es.aonsolutions.aio.test.product.ProductTestSuite;

@Suite
@SelectClasses({
	ConfigTestSuite.class,
	ProductTestSuite.class,
	InvoiceTestSuite.class
})
public class AonHibernateTestSuite {

	
}
