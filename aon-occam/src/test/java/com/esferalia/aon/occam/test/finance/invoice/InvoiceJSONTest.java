package com.esferalia.aon.occam.test.finance.invoice;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON.InvoiceJSONVersion;
import com.esferalia.aon.occam.api.model.AonAsserts;
import com.esferalia.aon.occam.api.model.AonMocker;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public class InvoiceJSONTest {

	@Test
	public void testNullInvoice() {
		Invoice to = null;
		Optional<JSONObject> optJson = InvoiceJSON.to(InvoiceJSONVersion.V2,to);
		assertTrue( optJson.isEmpty() );
	}

	@Test
	public void testInvoice() {
		Invoice originalInvoice = AonMocker.mock(Invoice.class);
		Optional<JSONObject> originalInvoiceOptJSON = InvoiceJSON.to(InvoiceJSONVersion.V2,originalInvoice);
		assertTrue( originalInvoiceOptJSON.isPresent() );
		JSONObject originalJSON = originalInvoiceOptJSON.get();
		assertNotNull(originalJSON);
		AonAsserts.assertNotEmptyKeys("InvoiceJSON", originalJSON);
		
		Optional<Invoice> targetOptInvoice = InvoiceJSON.from(InvoiceJSONVersion.V2,originalJSON);
		assertTrue( targetOptInvoice.isPresent() );
		Invoice targetInvoice = targetOptInvoice.get();
		Optional<JSONObject> targetInvoiceOptJSON = InvoiceJSON.to(InvoiceJSONVersion.V2,targetInvoice);
		assertTrue( targetInvoiceOptJSON.isPresent() );
		JSONObject targetJSON = targetInvoiceOptJSON.get();
		assertNotNull(targetJSON);
		
		assertTrue( originalJSON.similar(targetJSON));
		
	}
	
	
	
}
