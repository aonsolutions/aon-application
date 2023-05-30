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

import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.json.RegistryJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class RegistryJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Registry expected = RegistryJSON.from( json );
		assertNull(expected);
		expected = null;
		json = RegistryJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Registry expected = AonFaker.getRegistry( );
		JSONObject json = RegistryJSON.to(expected);
		Registry actual = RegistryJSON.from(json);
		Asserts.assertEqualsRegistry(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Registry> actual = RegistryJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getRegistry())
			.map(r -> RegistryJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Registry> actual = RegistryJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedRegistryJson = expected.getJSONObject(i);
				Registry expectedRegistry = RegistryJSON.from( expectedRegistryJson );
				Asserts.assertEqualsRegistry(actual.get(i), expectedRegistry);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Registry> expected = null;
		JSONArray array = RegistryJSON.to(expected);
		List<Registry> actual = RegistryJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Registry collections");
	}

	@Test
	void testListConvert() {
		List<Registry> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getRegistry())
			.toList()
		;
		JSONArray array = RegistryJSON.to(expected);
		List<Registry> actual = RegistryJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Registry collections");
	}
}
