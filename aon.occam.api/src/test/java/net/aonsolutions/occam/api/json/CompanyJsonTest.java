package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Company;

class CompanyJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Company to = AonMocker.mock(Company.class);
		JSONObject json = CompanyJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("CompanyJSON", json);
	}
	
	@Test
	void testNullCompany() {
		Company to = null;
		JSONObject json = CompanyJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Company geo = CompanyJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Company geo = CompanyJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Company to = AonMocker.mock(Company.class);
		JSONObject json = CompanyJSON.toJSON(to);
		assertNotNull(json);
		Company from = CompanyJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
