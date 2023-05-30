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

import net.aonsolutions.occam.api.invoicing.InvoiceTax;
import net.aonsolutions.occam.json.InvoiceTaxJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class InvoiceTaxJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		InvoiceTax expected = InvoiceTaxJSON.from( json );
		assertNull(expected);
		expected = null;
		json = InvoiceTaxJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		InvoiceTax expected = AonFaker.getInvoiceTax( );
		JSONObject json = InvoiceTaxJSON.to(expected);
		InvoiceTax actual = InvoiceTaxJSON.from(json);
		Asserts.assertEqualsInvoiceTax(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<InvoiceTax> actual = InvoiceTaxJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getInvoiceTax())
			.map(r -> InvoiceTaxJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<InvoiceTax> actual = InvoiceTaxJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedInvoiceTaxJson = expected.getJSONObject(i);
				InvoiceTax expectedInvoiceTax = InvoiceTaxJSON.from( expectedInvoiceTaxJson );
				Asserts.assertEqualsInvoiceTax(actual.get(i), expectedInvoiceTax);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<InvoiceTax> expected = null;
		JSONArray array = InvoiceTaxJSON.to(expected);
		List<InvoiceTax> actual = InvoiceTaxJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty InvoiceTax collections");
	}

	@Test
	void testListConvert() {
		List<InvoiceTax> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getInvoiceTax())
			.toList()
		;
		JSONArray array = InvoiceTaxJSON.to(expected);
		List<InvoiceTax> actual = InvoiceTaxJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON InvoiceTax collections");
	}
}
