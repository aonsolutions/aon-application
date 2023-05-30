package net.aonsolutions.occam.test.json;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.invoicing.Invoice;
import net.aonsolutions.occam.json.InvoiceJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class InvoiceJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Invoice expected = InvoiceJSON.from( json );
		assertNull(expected);
		expected = null;
		json = InvoiceJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Invoice expected = AonFaker.getInvoice( );
		JSONObject json = InvoiceJSON.to(expected);
		Invoice actual = InvoiceJSON.from(json);
		Asserts.assertEqualsInvoice(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Invoice> actual = InvoiceJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getInvoice())
			.map(r -> InvoiceJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Invoice> actual = InvoiceJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedInvoiceJson = expected.getJSONObject(i);
				Invoice expectedInvoice = InvoiceJSON.from( expectedInvoiceJson );
				Asserts.assertEqualsInvoice(actual.get(i), expectedInvoice);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Invoice> expected = null;
		JSONArray array = InvoiceJSON.to(expected);
		List<Invoice> actual = InvoiceJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Invoice collections");
	}

	@Test
	void testListConvert() {
		List<Invoice> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getInvoice())
			.toList()
		;
		JSONArray array = InvoiceJSON.to(expected);
		List<Invoice> actual = InvoiceJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Invoice collections");
	}
}
