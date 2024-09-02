package com.esferalia.aon.occam.test.json;


import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.json.invoice.InvoiceSeriesJSON;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;


public class JsonInvoiceSeriesTest extends AbstractOccamTest {

	@Test
	@RepeatedTest( 200 )
	public void test() {
		InvoiceSeries expected = AonFaker.getInvoiceSeries();
		JSONObject json = InvoiceSeriesJSON.to(expected);
		InvoiceSeries actual = InvoiceSeriesJSON.from(json);
		Asserts.assertEqualsInvoiceSeries(expected, actual);
		
		if (json != null) {
			actual = InvoiceSeriesJSON.from(json.toString());
			Asserts.assertEqualsInvoiceSeries(expected, actual);
		}
	}
	
}
