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

import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.json.AuditJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class AuditJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Audit expected = AuditJSON.from( json );
		assertNull(expected);
		expected = null;
		json = AuditJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Audit expected = AonFaker.getAudit( );
		JSONObject json = AuditJSON.to(expected);
		Audit actual = AuditJSON.from(json);
		Asserts.assertEqualsAudit(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Audit> actual = AuditJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getAudit())
			.map(s -> AuditJSON.to(s))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Audit> actual = AuditJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedAuditJson = expected.getJSONObject(i);
				Audit expectedAudit = AuditJSON.from( expectedAuditJson );
				Asserts.assertEqualsAudit(actual.get(i), expectedAudit);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Audit> expected = null;
		JSONArray array = AuditJSON.to(expected);
		List<Audit> actual = AuditJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Audit collections");
	}

	@Test
	void testListConvert() {
		List<Audit> expected = IntStream.range(0, AonRandom.getInt(0, 50))
			.mapToObj(i -> AonFaker.getAudit())
			.toList()
		;
		JSONArray array = AuditJSON.to(expected);
		List<Audit> actual = AuditJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Audit collections");
	}
}
