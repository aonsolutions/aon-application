package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Geozone;

class GeozoneJsonTest  {
	
	@Test
	void testEmptyJSONObjects() {
		Geozone to = AonMocker.mock(Geozone.class);
		JSONObject json = GeozoneJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("GeozoneJSON", json);
	}
	
	@Test
	void testNullGeozone() {
		Geozone to = null;
		JSONObject json = GeozoneJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Geozone geo = GeozoneJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Geozone geo = GeozoneJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Geozone to = AonMocker.mock(Geozone.class);
		JSONObject json = GeozoneJSON.toJSON(to);
		assertNotNull(json);
		Geozone from = GeozoneJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
