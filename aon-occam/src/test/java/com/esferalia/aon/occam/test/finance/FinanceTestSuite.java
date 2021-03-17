package com.esferalia.aon.occam.test.finance;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.finance.invoice.InvoiceTestSuite;
import com.esferalia.aon.occam.test.finance.paymethod.PayMethodTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	PayMethodTestSuite.class,
	InvoiceTestSuite.class
})
public class FinanceTestSuite {

	
}
