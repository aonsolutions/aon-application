package com.esferalia.aon.occam.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.accounting.AccountingTestSuite;
import com.esferalia.aon.occam.test.finance.FinanceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	FinanceTestSuite.class,
	AccountingTestSuite.class,
})
public class OccamTestSuite {

	
}
