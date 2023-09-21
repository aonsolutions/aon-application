package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.esferalia.aon.occam.test.product.TargetItemTest;

@RunWith(Suite.class)
@SuiteClasses({
	ValidationSaveEmptyDomainTest.class,
	ValidationSaveEmptyIssueDateTest.class,
	ValidationSaveEmptyTaxDateTest.class,
	ValidationSaveEmptyInvoiceTypeTest.class,
	ValidationSaveEmptyRegistryTest.class,
	InsertInvoiceTest.class,
	TargetItemTest.class
//	DeleteAllInvoicesTest.class,
})
public class InvoiceTestSuite {

	
}
