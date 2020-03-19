package com.esferalia.aon.occam.test.finance.invoice;


import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyTaxDateTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testValidationSaveEmptyTaxDate() {
		Invoice invoice = new Invoice();
		invoice.setDomain(DOMAIN_ID);
		invoice.setIssueDate( new Date() );
		AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
	}
	
}
