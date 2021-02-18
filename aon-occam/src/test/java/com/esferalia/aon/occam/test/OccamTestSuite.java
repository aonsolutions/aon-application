package com.esferalia.aon.occam.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.customer.CustomerTestSuite;
import com.esferalia.aon.occam.test.product.TariffTestSuite;
import com.esferalia.aon.occam.test.registry.RegistryTestSuite;
import com.esferalia.aon.occam.test.registry.media.RegistryMediaTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
//	AccountTestSuite.class,
	TariffTestSuite.class,
	RegistryTestSuite.class,
	RegistryMediaTestSuite.class,
	CustomerTestSuite.class,
//	FinanceTestSuite.class,
//	AccountingTestSuite.class,
//	AnalyticalAccountingTestSuite.class,
})
public class OccamTestSuite {

	
}
