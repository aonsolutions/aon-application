package com.esferalia.aon.occam.test.finance.invoice;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.invoice.InvoiceBreakdownJSON;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class InvoiceBreakdownJSONTest {

	@RepeatedTest( 100 )
	@Test
	public void test() {
		InvoiceBreakdown expected = AonRandom.getInvoiceBreakdown();
		JSONObject json = InvoiceBreakdownJSON.toJSON(expected);
		InvoiceBreakdown actual = InvoiceBreakdownJSON.fromJSON(json);
		Asserts.assertEqualsInvoiceBreakdown(expected, actual);
		
		if (json != null) {
			actual = InvoiceBreakdownJSON.fromString(json.toString());
			Asserts.assertEqualsInvoiceBreakdown(expected, actual);
		}
	}

}
