package com.esferalia.aon.occam.api.model.invoice;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.raw.InvoiceJSON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.InvoiceAsserts;

public class InvoiceTest extends AbstractOccamTest {
	
	@Test
	public void testGetter() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to);
		Invoice from = InvoiceJSON.fromJSON(json);
		InvoiceAsserts.assertEqualsInvoice(to, from, false);
	}

}
