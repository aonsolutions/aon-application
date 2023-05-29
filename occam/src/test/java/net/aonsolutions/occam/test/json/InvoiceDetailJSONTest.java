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

import net.aonsolutions.occam.api.invoicing.InvoiceDetail;
import net.aonsolutions.occam.json.InvoiceDetailJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class InvoiceDetailJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		InvoiceDetail expected = InvoiceDetailJSON.from( json );
		assertNull(expected);
		expected = null;
		json = InvoiceDetailJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		InvoiceDetail expected = AonFaker.getInvoiceDetail( );
		JSONObject json = InvoiceDetailJSON.to(expected);
		InvoiceDetail actual = InvoiceDetailJSON.from(json);
		Asserts.assertEqualsInvoiceDetail(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<InvoiceDetail> actual = InvoiceDetailJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getInvoiceDetail())
			.map(r -> InvoiceDetailJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<InvoiceDetail> actual = InvoiceDetailJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedInvoiceDetailJson = expected.getJSONObject(i);
				InvoiceDetail expectedInvoiceDetail = InvoiceDetailJSON.from( expectedInvoiceDetailJson );
				Asserts.assertEqualsInvoiceDetail(actual.get(i), expectedInvoiceDetail);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<InvoiceDetail> expected = null;
		JSONArray array = InvoiceDetailJSON.to(expected);
		List<InvoiceDetail> actual = InvoiceDetailJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty InvoiceDetail collections");
	}

	@Test
	void testListConvert() {
		List<InvoiceDetail> expected = IntStream.range(1, AonRandom.getInt(2, 50))
			.mapToObj(i -> AonFaker.getInvoiceDetail())
			.toList()
		;
		JSONArray array = InvoiceDetailJSON.to(expected);
		List<InvoiceDetail> actual = InvoiceDetailJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON InvoiceDetail collections");
	}
}
