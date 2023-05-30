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

import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.json.ApplicationParameterJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class ApplicationParameterJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		ApplicationParameter expected = ApplicationParameterJSON.from( json );
		assertNull(expected);
		expected = null;
		json = ApplicationParameterJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		ApplicationParameter expected = AonFaker.getApplicationParameter( );
		JSONObject json = ApplicationParameterJSON.to(expected);
		ApplicationParameter actual = ApplicationParameterJSON.from(json);
		Asserts.assertEqualsApplicationParameter(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<ApplicationParameter> actual = ApplicationParameterJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getApplicationParameter())
			.map(domain -> ApplicationParameterJSON.to(domain))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<ApplicationParameter> actual = ApplicationParameterJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedApplicationParameterJson = expected.getJSONObject(i);
				ApplicationParameter expectedApplicationParameter = ApplicationParameterJSON.from( expectedApplicationParameterJson );
				Asserts.assertEqualsApplicationParameter(actual.get(i), expectedApplicationParameter);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<ApplicationParameter> expected = null;
		JSONArray array = ApplicationParameterJSON.to(expected);
		List<ApplicationParameter> actual = ApplicationParameterJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty ApplicationParameter collections");
	}

	@Test
	void testListConvert() {
		List<ApplicationParameter> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getApplicationParameter())
			.toList()
		;
		JSONArray array = ApplicationParameterJSON.to(expected);
		List<ApplicationParameter> actual = ApplicationParameterJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON ApplicationParameter collections");
	}
}
