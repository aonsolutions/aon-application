package com.esferalia.aon.occam.test.json;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	JsonAccountTest.class,
	JsonAccountPeriodTest.class,
	JsonAccountingReportParamsTest.class,
	JsonAccountTrialBalanceTest.class,
	JsonAccountTrialBalanceReportTest.class,
	JsonAccountOperatingReportParams.class,
	JsonAccountPeriodsTest.class
})
public class JsonTestSuite {

	
}
