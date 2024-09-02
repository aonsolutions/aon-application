package com.esferalia.aon.occam.test.finance.invoice;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyInvoiceTypeTest extends AbstractOccamTest {

	@Test
	public void testValidationSaveEmptyInvoiceType() {
		AonConfiguration config = AON.getConfiguration(ctx,null);
		InvoiceFakerParams params = new InvoiceFakerParams(ctx, config);
		Invoice invoice = InvoiceFaker.getRandom(params);
		invoice.setType(null);
		assertThrows(AonCoreException.class, () ->
			AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice));
	}
	
}
