package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Domain;

class DomainJsonTest  {
	
	@Test
	void testEmptyJSONObjects() {
		Domain to = AonMocker.mock(Domain.class);
		JSONObject json = DomainJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("DomainJSON", json);
	}
	
	@Test
	void testNullDomain() {
		Domain to = null;
		JSONObject json = DomainJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Domain geo = DomainJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Domain geo = DomainJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 50 )
	void testFromTo() {
		Domain to = AonMocker.mock(Domain.class);
		JSONObject json = DomainJSON.toJSON(to);
		assertNotNull(json);
		Domain from = DomainJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
