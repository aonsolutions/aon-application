package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.occam.api.json.invoice.InvoiceMinJSON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceMin;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;

class InvoiceMinJSONTest extends AbstractOccamTest{

	@RepeatedTest( 10 )
	void fromJSON() {
		Invoice expected = InvoiceFaker.getRandom( ctx );
		InvoiceMin im = InvoiceMin.to(expected); 
		JSONObject json = InvoiceMinJSON.toJSON(im);
		InvoiceMin actual = InvoiceMinJSON.fromJSON(json);
		Asserts.assertEqualsInvoiceMin(im, actual);
		if (json != null) {
			actual = InvoiceMinJSON.fromString(json.toString());
			Asserts.assertEqualsInvoiceMin(im, actual);
		}
	}
	
	
}
