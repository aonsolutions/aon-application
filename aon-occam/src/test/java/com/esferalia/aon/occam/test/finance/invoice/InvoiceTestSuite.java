package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.esferalia.aon.occam.test.product.TargetItemTest;

@Suite
@SelectClasses({
	InvoiceBreakdownJSONTest.class,
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
