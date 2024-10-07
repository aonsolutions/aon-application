package com.esferalia.aon.occam.api.json.raw;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.invoice.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetail;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFaker;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.InvoiceAsserts;
import com.esferalia.aon.occam.test.Repeat;

public class InvoiceJsonTest extends AbstractOccamTest {
	
	@Test
	public void testEmptyJSONObjects() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to);
		assertNotNull(json);
		Asserts.assertNotEmptyKeys("InvoiceJSON", json);
	}
	
	@Test
	public void testNullInvoice() {
		Invoice to = null;
		JSONObject json = InvoiceJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	public void testNullString() {
		String to = null;
		Invoice invoice = InvoiceJSON.fromJSON( to );
		assertNull(invoice);
	}

	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Invoice invoice = InvoiceJSON.fromJSON( to );
		assertNull(invoice);
	}

	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Invoice invoice = InvoiceJSON.fromJSON( to );
		assertNull(invoice);
	}

	
	@Test
	public void testNullInvoiceDetail() {
		InvoiceDetail detail = null;
		JSONObject json = InvoiceDetailJSON.toJSON( detail);
		assertNull(json);
	}
	
	@Test
	public void testNullJSONDetail() {
		Invoice invoice = null;
		JSONObject to = null;
		InvoiceDetail invoiceDetail = InvoiceDetailJSON.fromJSON( invoice, to );
		assertNull(invoiceDetail);
	}

	@Test
	public void testEmptyJSONDetail() {
		Invoice invoice = null;
		JSONObject to = new JSONObject();
		InvoiceDetail invoiceDetail = InvoiceDetailJSON.fromJSON( invoice, to );
		assertNull(invoiceDetail);
	}
	
	
	@Test
	@Repeat( 20 )
	public void testFromTo() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to);
		assertNotNull(json);
		Invoice from = InvoiceJSON.fromJSON(json);
		InvoiceAsserts.assertEqualsInvoice(to, from, false);
	}

	@Test
	@Repeat( 20 )
	public void testFromToWithComments() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to, true);
		assertNotNull(json);
		Invoice from = InvoiceJSON.fromJSON(json);
		InvoiceAsserts.assertEqualsInvoice(to, from, false);
	}

	@Test
	@Repeat( 25 )
	public void testFromStringTo() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to);
		assertNotNull(json);
		String jsonString = json.toString();
		Invoice from = InvoiceJSON.fromJSON(jsonString);
		InvoiceAsserts.assertEqualsInvoice(to, from, false);
	}
	
}
