package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testValidationSaveEmptyDomain() {
		Invoice invoice = new Invoice();
		AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
	}
	
}
