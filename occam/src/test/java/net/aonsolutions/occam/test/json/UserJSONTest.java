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

import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.json.UserJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class UserJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		User expected = UserJSON.from( json );
		assertNull(expected);
		expected = null;
		json = UserJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		User expected = AonFaker.getUser( );
		JSONObject json = UserJSON.to(expected);
		User actual = UserJSON.from(json);
		Asserts.assertEqualsUser(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<User> actual = UserJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getUser())
			.map(user -> UserJSON.to(user))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<User> actual = UserJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedUserJson = expected.getJSONObject(i);
				User expectedUser = UserJSON.from( expectedUserJson );
				Asserts.assertEqualsUser(actual.get(i), expectedUser);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<User> expected = null;
		JSONArray array = UserJSON.to(expected);
		List<User> actual = UserJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty User collections");
	}

	@Test
	void testListConvert() {
		List<User> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getUser())
			.toList()
		;
		JSONArray array = UserJSON.to(expected);
		List<User> actual = UserJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON User collections");
	}
}
