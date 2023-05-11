package net.aonsolutions.occam.test.json;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.json.BookingJSON;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)
class BookingJSONTest extends AbstractOccamTest {

	@RepeatedTest(20)
	void testSimpleConvert() {
		Booking expected = AonFaker.getBooking( );
		JSONObject json = BookingJSON.to(expected);
		Booking actual = BookingJSON.from(json);
		Asserts.assertEqualsBooking(expected, actual);
	}
	
	@Test
	void testEmptyArrayConvert() {
		JSONArray expected = null;
		List<Booking> actual = BookingJSON.from(expected);
		assertNotNull( actual );
		assertTrue(actual.isEmpty());
	}

	@Test
	void testArrayConvert() {
		JSONArray expected = IntStream.range(0, AonRandom.getInt(0, 50))
			.mapToObj(i -> AonFaker.getBooking())
			.map(booking -> BookingJSON.to(booking))
			.collect( JSONArray::new,JSONArray::put,JSONArray::put )
		;
		assertNotNull( expected );
		List<Booking> actual = BookingJSON.from(expected);
		assertNotNull( actual );
		assertEquals(expected.length(), actual.size());
		IntStream.range(0, expected.length())
			.forEach(i -> {
				JSONObject expectedBookingJson = expected.getJSONObject(i);
				Booking expectedBooking = BookingJSON.from( expectedBookingJson );
				Asserts.assertEqualsBooking(actual.get(i), expectedBooking);		
			});
	}

	@Test
	void testEmptyListConvert() {
		List<Booking> expected = null;
		JSONArray array = BookingJSON.to(expected);
		List<Booking> actual = BookingJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Empty Booking collections");
	}

	@Test
	void testListConvert() {
		List<Booking> expected = IntStream.range(0, AonRandom.getInt(0, 50))
			.mapToObj(i -> AonFaker.getBooking())
			.toList()
		;
		JSONArray array = BookingJSON.to(expected);
		List<Booking> actual = BookingJSON.from(array);
		Asserts.assertEqualsCollection(expected, actual, "JSON Domain collections");
	}
}
