package com.esferalia.aon.occam.test.finance.invoice;


import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyRegistryTest extends AbstractOccamTest {

	@Test
	public void testValidationSaveEmptyRegistry() {
		Invoice invoice = new Invoice();
		invoice.setDomain(DOMAIN_ID);
		invoice.setIssueDate( new Date() );
		invoice.setTaxDate( new Date() );
		invoice.setType(InvoiceType.SALES);
		assertThrows(AonCoreException.class, () ->
			AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice));
	}
	
}
