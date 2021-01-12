package com.esferalia.aon.occam.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.registry.RegistryTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	RegistryTestSuite.class,
//	FinanceTestSuite.class,
//	AccountingTestSuite.class,
//	AnalyticalAccountingTestSuite.class,
})
public class OccamTestSuite {

	
}
