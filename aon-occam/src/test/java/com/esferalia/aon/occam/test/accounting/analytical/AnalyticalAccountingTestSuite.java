package com.esferalia.aon.occam.test.accounting.analytical;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
	AnalyticalAccountingCreateTest.class,
	AnalyticalAccountingGetTest.class,
	AnalyticalAccountingReportTest.class,
})
public class AnalyticalAccountingTestSuite {

	
}
