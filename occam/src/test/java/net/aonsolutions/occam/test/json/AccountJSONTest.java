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

import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.json.AccountJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class AccountJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Account expected = AccountJSON.from( json );
		assertNull(expected);
		expected = null;
		json = AccountJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Account expected = AonFaker.getAccount( );
		JSONObject json = AccountJSON.to(expected);
		Account actual = AccountJSON.from(json);
		Asserts.assertEqualsAccount(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Account> actual = AccountJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.number(1, 50))
			.mapToObj(i -> AonFaker.getAccount())
			.map(r -> AccountJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Account> actual = AccountJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedAccountJson = expected.getJSONObject(i);
				Account expectedAccount = AccountJSON.from( expectedAccountJson );
				Asserts.assertEqualsAccount(actual.get(i), expectedAccount);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Account> expected = null;
		JSONArray array = AccountJSON.to(expected);
		List<Account> actual = AccountJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Account collections");
	}

	@Test
	void testListConvert() {
		List<Account> expected = IntStream.range(1, AonRandom.number(2, 50))
			.mapToObj(i -> AonFaker.getAccount())
			.toList()
		;
		JSONArray array = AccountJSON.to(expected);
		List<Account> actual = AccountJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Registry collections");
	}
}
