package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Iae;

class IaeJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Iae to = AonMocker.mock(Iae.class);
		JSONObject json = IaeJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("IaeJSON", json);
	}
	
	@Test
	void testNullIae() {
		Iae to = null;
		JSONObject json = IaeJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Iae geo = IaeJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Iae geo = IaeJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Iae to = AonMocker.mock(Iae.class);
		JSONObject json = IaeJSON.toJSON(to);
		assertNotNull(json);
		Iae from = IaeJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
