package com.esferalia.aon.occam.test.accounting;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.accounting.entry.AccountEntryTestSuite;
import com.esferalia.aon.occam.test.accounting.period.AccountPeriodTestSuite;

@Suite
@SelectClasses({
	AccountPeriodTestSuite.class,
	AccountEntryTestSuite.class,
	// BalanceTestSuite.class
})
public class AccountingTestSuite {

	
}
