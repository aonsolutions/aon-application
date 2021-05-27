package com.esferalia.aon.occam.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.accounting.AccountingTestSuite;
import com.esferalia.aon.occam.test.accounting.account.AccountTestSuite;
import com.esferalia.aon.occam.test.finance.FinanceTestSuite;
import com.esferalia.aon.occam.test.json.JsonTestSuite;
import com.esferalia.aon.occam.test.product.ProductTestSuite;
import com.esferalia.aon.occam.test.product.TariffTestSuite;
import com.esferalia.aon.occam.test.registry.RegistryTestSuite;
import com.esferalia.aon.occam.test.registry.address.RegistryAddressTestSuite;
import com.esferalia.aon.occam.test.registry.creditor.CreditorTestSuite;
import com.esferalia.aon.occam.test.registry.customer.CustomerTestSuite;
import com.esferalia.aon.occam.test.registry.media.RegistryMediaTestSuite;
import com.esferalia.aon.occam.test.registry.supplier.SupplierTestSuite;
import com.esferalia.aon.occam.test.registry.task_holder.TaskHolderTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	AccountTestSuite.class,
	TariffTestSuite.class,
	ProductTestSuite.class,
	RegistryTestSuite.class,
	RegistryAddressTestSuite.class,
	RegistryMediaTestSuite.class,
	CustomerTestSuite.class,
	CreditorTestSuite.class,
	SupplierTestSuite.class,
	TaskHolderTestSuite.class,
	FinanceTestSuite.class,
	AccountingTestSuite.class,
//	JsonTestSuite.class,
//	AnalyticalAccountingTestSuite.class,
})
public class OccamTestSuite {

	
}
