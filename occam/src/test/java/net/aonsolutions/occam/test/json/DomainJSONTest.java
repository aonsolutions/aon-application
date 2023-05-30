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

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.json.DomainJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class DomainJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Domain expected = DomainJSON.from( json );
		assertNull(expected);
		expected = null;
		json = DomainJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Domain expected = AonFaker.getDomain( );
		JSONObject json = DomainJSON.to(expected);
		Domain actual = DomainJSON.from(json);
		Asserts.assertEqualsDomain(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Domain> actual = DomainJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getDomain())
			.map(domain -> DomainJSON.to(domain))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Domain> actual = DomainJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedDomainJson = expected.getJSONObject(i);
				Domain expectedDomain = DomainJSON.from( expectedDomainJson );
				Asserts.assertEqualsDomain(actual.get(i), expectedDomain);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Domain> expected = null;
		JSONArray array = DomainJSON.to(expected);
		List<Domain> actual = DomainJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Domain collections");
	}

	@Test
	void testListConvert() {
		List<Domain> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getDomain())
			.toList()
		;
		JSONArray array = DomainJSON.to(expected);
		List<Domain> actual = DomainJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Domain collections");
	}
}
