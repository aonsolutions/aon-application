package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Cnae;

class CnaeJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Cnae to = AonMocker.mock(Cnae.class);
		JSONObject json = CnaeJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("CnaeJSON", json);
	}
	
	@Test
	void testNullCnae() {
		Cnae to = null;
		JSONObject json = CnaeJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Cnae geo = CnaeJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Cnae geo = CnaeJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Cnae to = AonMocker.mock(Cnae.class);
		JSONObject json = CnaeJSON.toJSON(to);
		assertNotNull(json);
		Cnae from = CnaeJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
