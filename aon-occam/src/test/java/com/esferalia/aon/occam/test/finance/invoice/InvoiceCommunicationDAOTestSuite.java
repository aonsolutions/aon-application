package com.esferalia.aon.occam.test.finance.invoice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InvoiceCommunicationDAOGetConfigTest.class,
	InvoiceCommunicationDAOSaveConfigTest.class,
	InvoiceCommunicationDAOGetInvoicesTest.class,
	InvoiceCommunicationDAOGetHistoryTest.class,
	InvoiceCommunicationDAOValidationTest.class,
})
public class InvoiceCommunicationDAOTestSuite {

}
