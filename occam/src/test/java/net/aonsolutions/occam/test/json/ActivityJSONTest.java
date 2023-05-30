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

import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.json.ActivityJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class ActivityJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Activity expected = ActivityJSON.from( json );
		assertNull(expected);
		expected = null;
		json = ActivityJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Activity expected = AonFaker.getActivity( );
		JSONObject json = ActivityJSON.to(expected);
		Activity actual = ActivityJSON.from(json);
		Asserts.assertEqualsActivity(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Activity> actual = ActivityJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getActivity())
			.map(r -> ActivityJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Activity> actual = ActivityJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedActivityJson = expected.getJSONObject(i);
				Activity expectedActivity = ActivityJSON.from( expectedActivityJson );
				Asserts.assertEqualsActivity(actual.get(i), expectedActivity);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Activity> expected = null;
		JSONArray array = ActivityJSON.to(expected);
		List<Activity> actual = ActivityJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Activity collections");
	}

	@Test
	void testListConvert() {
		List<Activity> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getActivity())
			.toList()
		;
		JSONArray array = ActivityJSON.to(expected);
		List<Activity> actual = ActivityJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Registry collections");
	}
}
