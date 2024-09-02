package com.esferalia.aon.occam.test.finance;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.finance.invoice.InvoiceTestSuite;
import com.esferalia.aon.occam.test.finance.paymethod.PayMethodTestSuite;

@Suite
@SelectClasses({
	PayMethodTestSuite.class,
	InvoiceTestSuite.class
})
public class FinanceTestSuite {

	
}
