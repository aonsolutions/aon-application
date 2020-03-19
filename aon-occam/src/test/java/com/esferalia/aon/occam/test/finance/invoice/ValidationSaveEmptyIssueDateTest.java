package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyIssueDateTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testValidationSaveEmptyIssueDate() {
		Invoice invoice = new Invoice();
		invoice.setDomain(DOMAIN_ID);
		AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
	}
	
}
