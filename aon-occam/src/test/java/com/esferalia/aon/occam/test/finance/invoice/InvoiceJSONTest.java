package com.esferalia.aon.occam.test.finance.invoice;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public class InvoiceJSONTest {

	
	@Test
	public void fromJSON() {
		emptyJSON();
	}
	
	@Test
	public void toJSON() {
		emptyInvoice();
	}
	
	private void emptyJSON() {
		InvoiceJSON.fromJSON(new JSONObject());
	}
	
	private void emptyInvoice() {
		InvoiceJSON.toJSON(new Invoice());
	}
}
