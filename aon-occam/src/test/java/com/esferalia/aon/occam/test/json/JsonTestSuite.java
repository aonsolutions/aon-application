package com.esferalia.aon.occam.test.json;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	JsonSeriesTest.class,
	JsonInvoiceSeriesTest.class,
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
