package com.esferalia.aon.occam.test.accounting;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.accounting.entry.AccountEntryTestSuite;
import com.esferalia.aon.occam.test.accounting.period.AccountPeriodTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	AccountPeriodTestSuite.class,
	AccountEntryTestSuite.class,
	AmortizationTypeTest.class
	// BalanceTestSuite.class
})
public class AccountingTestSuite {

	
}
