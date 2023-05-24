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

import net.aonsolutions.occam.api.accounting.AccountingPeriod;
import net.aonsolutions.occam.json.AccountingPeriodJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class AccountingPeriodJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		AccountingPeriod expected = AccountingPeriodJSON.from( json );
		assertNull(expected);
		expected = null;
		json = AccountingPeriodJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		AccountingPeriod expected = AonFaker.getAccountingPeriod( );
		JSONObject json = AccountingPeriodJSON.to(expected);
		AccountingPeriod actual = AccountingPeriodJSON.from(json);
		Asserts.assertEqualsAccountingPeriod(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<AccountingPeriod> actual = AccountingPeriodJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getAccountingPeriod())
			.map(r -> AccountingPeriodJSON.to(r))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<AccountingPeriod> actual = AccountingPeriodJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedAccountingPeriodJson = expected.getJSONObject(i);
				AccountingPeriod expectedAccountingPeriod = AccountingPeriodJSON.from( expectedAccountingPeriodJson );
				Asserts.assertEqualsAccountingPeriod(actual.get(i), expectedAccountingPeriod);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<AccountingPeriod> expected = null;
		JSONArray array = AccountingPeriodJSON.to(expected);
		List<AccountingPeriod> actual = AccountingPeriodJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty AccountingPeriod collections");
	}

	@Test
	void testListConvert() {
		List<AccountingPeriod> expected = IntStream.range(1, AonRandom.getInt(2, 50))
			.mapToObj(i -> AonFaker.getAccountingPeriod())
			.toList()
		;
		JSONArray array = AccountingPeriodJSON.to(expected);
		List<AccountingPeriod> actual = AccountingPeriodJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Registry collections");
	}
}
