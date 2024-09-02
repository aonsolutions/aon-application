package com.esferalia.aon.occam.test.finance.invoice;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test
	public void testValidationSaveEmptyDomain() {
		Invoice invoice = new Invoice();
		assertThrows(AonCoreException.class, () ->
			AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice));
	}
	
}
