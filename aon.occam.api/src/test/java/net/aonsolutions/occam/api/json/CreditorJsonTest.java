package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Creditor;

class CreditorJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Creditor to = AonMocker.mock(Creditor.class);
		JSONObject json = CreditorJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("CreditorJSON", json);
	}
	
	@Test
	void testNullCreditor() {
		Creditor to = null;
		JSONObject json = CreditorJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Creditor geo = CreditorJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Creditor geo = CreditorJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Creditor to = AonMocker.mock(Creditor.class);
		JSONObject json = CreditorJSON.toJSON(to);
		assertNotNull(json);
		Creditor from = CreditorJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
