package com.esferalia.aon.occam.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.api.model.invoice.InvoiceTestSuite;
import com.esferalia.aon.occam.test.accounting.AccountingTestSuite;
import com.esferalia.aon.occam.test.accounting.account.AccountTestSuite;
import com.esferalia.aon.occam.test.delivery.DeliveryTest;
import com.esferalia.aon.occam.test.finance.FinanceTestSuite;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.occam.test.json.JsonTestSuite;
import com.esferalia.aon.occam.test.marketing.MarketingTestSuite;
import com.esferalia.aon.occam.test.offer.OfferTest;
import com.esferalia.aon.occam.test.payroll.PayrollTestSuite;
import com.esferalia.aon.occam.test.product.ProductTestSuite;
import com.esferalia.aon.occam.test.product.TariffTestSuite;
import com.esferalia.aon.occam.test.project.ProjectTestSuite;
import com.esferalia.aon.occam.test.ql.AonOccamQLSuite;
import com.esferalia.aon.occam.test.registry.RegistryTestSuite;
import com.esferalia.aon.occam.test.registry.address.RegistryAddressTestSuite;
import com.esferalia.aon.occam.test.registry.company.CompanyTestSuite;
import com.esferalia.aon.occam.test.registry.creditor.CreditorTestSuite;
import com.esferalia.aon.occam.test.registry.customer.CustomerTestSuite;
import com.esferalia.aon.occam.test.registry.media.RegistryMediaTestSuite;
import com.esferalia.aon.occam.test.registry.supplier.SupplierTestSuite;
import com.esferalia.aon.occam.test.registry.task_holder.TaskHolderTestSuite;
import com.esferalia.aon.occam.test.workgroup.WorkgroupTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	CompanyTestSuite.class,
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
	JsonTestSuite.class,
	AonOccamQLSuite.class,
	AccountingTestSuite.class,
	WorkgroupTestSuite.class,
	ProjectTestSuite.class,
	PayrollTestSuite.class,
	FiscalTestSuite.class,
//	AnalyticalAccountingTestSuite.class,
	OfferTest.class,
	DeliveryTest.class,
	MarketingTestSuite.class,
	
	// Nuevos cambios en Occam
	InvoiceTestSuite.class
})
public class OccamTestSuite {

	
}
