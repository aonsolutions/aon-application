package es.aonsolutions.aio.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.aonsolutions.aio.test.config.ConfigTestSuite;
import es.aonsolutions.aio.test.invoice.InvoiceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	ConfigTestSuite.class,
	InvoiceTestSuite.class
})
public class AonHibernateTestSuite {

	
}
