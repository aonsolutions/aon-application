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

import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.json.GeozoneJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class GeozoneJSONTest extends AbstractOccamTest {

	@Test
	void testNullConvert() {
		JSONObject json = null;
		Geozone expected = GeozoneJSON.from( json );
		assertNull(expected);
		expected = null;
		json = GeozoneJSON.to( expected );
		assertNull(json);
	}

	@RepeatedTest(20)
	void testSimpleConvert() {
		Geozone expected = AonFaker.getGeoZone( );
		JSONObject json = GeozoneJSON.to(expected);
		Geozone actual = GeozoneJSON.from(json);
		Asserts.assertEqualsGeoZone(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Geozone> actual = GeozoneJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(1, 50))
			.mapToObj(i -> AonFaker.getGeoZone())
			.map(s -> GeozoneJSON.to(s))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Geozone> actual = GeozoneJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedGeoZoneJson = expected.getJSONObject(i);
				Geozone expectedGeoZone = GeozoneJSON.from( expectedGeoZoneJson );
				Asserts.assertEqualsGeoZone(actual.get(i), expectedGeoZone);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Geozone> expected = null;
		JSONArray array = GeozoneJSON.to(expected);
		List<Geozone> actual = GeozoneJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty GeoZone collections");
	}

	@Test
	void testListConvert() {
		List<Geozone> expected = IntStream.range(1, AonRandom.getInt(2, 50))
			.mapToObj(i -> AonFaker.getGeoZone())
			.toList()
		;
		JSONArray array = GeozoneJSON.to(expected);
		List<Geozone> actual = GeozoneJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Scope collections");
	}
}
