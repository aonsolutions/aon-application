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

import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.json.RegistryAddressJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class RegistryAddressJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		RegistryAddress expected = RegistryAddressJSON.from( json );
		assertNull(expected);
		expected = null;
		json = RegistryAddressJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		RegistryAddress expected = AonFaker.getRegistryAddress( );
		JSONObject json = RegistryAddressJSON.to(expected);
		RegistryAddress actual = RegistryAddressJSON.from(json);
		Asserts.assertEqualsRegistryAddress(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<RegistryAddress> actual = RegistryAddressJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getRegistryAddress())
			.map(r -> RegistryAddressJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<RegistryAddress> actual = RegistryAddressJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedRegistryAddressJson = expected.getJSONObject(i);
				RegistryAddress expectedRegistryAddress = RegistryAddressJSON.from( expectedRegistryAddressJson );
				Asserts.assertEqualsRegistryAddress(actual.get(i), expectedRegistryAddress);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<RegistryAddress> expected = null;
		JSONArray array = RegistryAddressJSON.to(expected);
		List<RegistryAddress> actual = RegistryAddressJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty RegistryAddress collections");
	}

	@Test
	void testListConvert() {
		List<RegistryAddress> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getRegistryAddress())
			.toList()
		;
		JSONArray array = RegistryAddressJSON.to(expected);
		List<RegistryAddress> actual = RegistryAddressJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON RegistryAddress collections");
	}
}
