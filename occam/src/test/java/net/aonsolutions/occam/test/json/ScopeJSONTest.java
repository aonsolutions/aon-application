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

import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.json.ScopeJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class ScopeJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Scope expected = ScopeJSON.from( json );
		assertNull(expected);
		expected = null;
		json = ScopeJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Scope expected = AonFaker.getScope( );
		JSONObject json = ScopeJSON.to(expected);
		Scope actual = ScopeJSON.from(json);
		Asserts.assertEqualsScope(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Scope> actual = ScopeJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getScope())
			.map(s -> ScopeJSON.to(s))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Scope> actual = ScopeJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedScopeJson = expected.getJSONObject(i);
				Scope expectedScope = ScopeJSON.from( expectedScopeJson );
				Asserts.assertEqualsScope(actual.get(i), expectedScope);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Scope> expected = null;
		JSONArray array = ScopeJSON.to(expected);
		List<Scope> actual = ScopeJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Scope collections");
	}

	@Test
	void testListConvert() {
		List<Scope> expected = IntStream.range(0, AonRandom.getInt(0, 50))
			.mapToObj(i -> AonFaker.getScope())
			.toList()
		;
		JSONArray array = ScopeJSON.to(expected);
		List<Scope> actual = ScopeJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Scope collections");
	}
}
