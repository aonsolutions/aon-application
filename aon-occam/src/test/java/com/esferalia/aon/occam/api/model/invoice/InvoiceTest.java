package com.esferalia.aon.occam.api.model.invoice;

import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.raw.InvoiceJSON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.InvoiceAsserts;
import com.esferalia.aon.occam.test.Repeat;

public class InvoiceTest extends AbstractOccamTest {
	
	@Test
	public void testEmptyJSONObjects() {
		Invoice to = InvoiceFaker.getRandom(ctx);
		JSONObject json = InvoiceJSON.toJSON(to);
		assertNotNull(json);
		Asserts.assertNotEmptyKeys("InvoiceJSON", json);
	}
	
	@Test
	@Repeat( 100 )
	public void testGetter() {
		Invoice to = InvoiceFaker.getRandom(ctx);
//		InvoiceTextPrinter.print(to);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		JSONObject json = InvoiceJSON.toJSON(to);
//		System.out.println( json.toString(1) );
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		Invoice from = InvoiceJSON.fromJSON(json);
//		InvoiceTextPrinter.print(from);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		InvoiceAsserts.assertEqualsInvoice(to, from, false);
		
		
	}

}
