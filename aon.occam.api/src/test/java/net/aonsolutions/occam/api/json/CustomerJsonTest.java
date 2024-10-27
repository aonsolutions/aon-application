package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;
import net.aonsolutions.occam.api.model.Customer;

class CustomerJsonTest {
	
	@Test
	void testEmptyJSONObjects() {
		Customer to = AonMocker.mock(Customer.class);
		JSONObject json = CustomerJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("CustomerJSON", json);
	}
	
	@Test
	void testNullCustomer() {
		Customer to = null;
		JSONObject json = CustomerJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Customer geo = CustomerJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Customer geo = CustomerJSON.fromJSON( to );
		assertNull(geo);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Customer to = AonMocker.mock(Customer.class);
		JSONObject json = CustomerJSON.toJSON(to);
		assertNotNull(json);
		Customer from = CustomerJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
