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

import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.json.DomainAuditJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class DomainAuditJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		DomainAudit expected = DomainAuditJSON.from( json );
		assertNull(expected);
		expected = null;
		json = DomainAuditJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		DomainAudit expected = AonFaker.getDomainAudit( );
		JSONObject json = DomainAuditJSON.to(expected);
		DomainAudit actual = DomainAuditJSON.from(json);
		Asserts.assertEqualsDomainAudit(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<DomainAudit> actual = DomainAuditJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getDomainAudit())
			.map(s -> DomainAuditJSON.to(s))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<DomainAudit> actual = DomainAuditJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedDomainAuditJson = expected.getJSONObject(i);
				DomainAudit expectedDomainAudit = DomainAuditJSON.from( expectedDomainAuditJson );
				Asserts.assertEqualsDomainAudit(actual.get(i), expectedDomainAudit);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<DomainAudit> expected = null;
		JSONArray array = DomainAuditJSON.to(expected);
		List<DomainAudit> actual = DomainAuditJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty DomainAudit collections");
	}

	@Test
	void testListConvert() {
		List<DomainAudit> expected = IntStream.range(0, AonRandom.getInt(0, 50))
			.mapToObj(i -> AonFaker.getDomainAudit())
			.toList()
		;
		JSONArray array = DomainAuditJSON.to(expected);
		List<DomainAudit> actual = DomainAuditJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Audit collections");
	}
}
