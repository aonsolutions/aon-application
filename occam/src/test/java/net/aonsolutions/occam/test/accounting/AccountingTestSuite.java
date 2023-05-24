package net.aonsolutions.occam.test.accounting;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	 AccountTest.class
	 ,AccountingPeriodTest.class
})
public class AccountingTestSuite {

	
}
