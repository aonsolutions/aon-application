package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Registry;

class RegistryJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Registry to = AonMocker.mock(Registry.class);
		JSONObject json = RegistryJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("RegistryJSON", json);
	}
	
	@Test
	void testNullRegistry() {
		Registry to = null;
		JSONObject json = RegistryJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Registry geo = RegistryJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Registry geo = RegistryJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Registry to = AonMocker.mock(Registry.class);
		JSONObject json = RegistryJSON.toJSON(to);
		assertNotNull(json);
		Registry from = RegistryJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
