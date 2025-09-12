package net.aonsolutions.aon.invoice.communication.exceptions;


import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;

public class InvoiceJSONTest extends AbstractVerifactuTest {

	private static final Integer INVOICE_ID = 476;

	@Test
	public void oldWay() {
		Invoice invoice = AON.getInvoice( getOccam() , INVOICE_ID);
		JSONObject json = InvoiceJSON.toJSON(invoice);
		System.out.println(json.toString(2));
	}
	
	
	
}
