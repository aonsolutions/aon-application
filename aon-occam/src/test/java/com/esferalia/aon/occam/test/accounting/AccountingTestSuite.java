package com.esferalia.aon.occam.test.accounting;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.accounting.period.AccountPeriodTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	AccountPeriodTestSuite.class,
//	AccountTestSuite.class,
//	AccountEntryTestSuite.class
})
public class AccountingTestSuite {

	
}
