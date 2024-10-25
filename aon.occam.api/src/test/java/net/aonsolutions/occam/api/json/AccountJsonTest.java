 package net.aonsolutions.occam.api.json;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonMocker;

class AccountJsonTest  {
	
	@Test
	void testEmptyJSONObjects() {
		Account to = AonMocker.mock(Account.class);
		JSONObject json = AccountJSON.toJSON(to);
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("AccountJSON", json);
	}
	
	@Test
	void testNullAccount() {
		Account to = null;
		JSONObject json = AccountJSON.toJSON( to );
		assertNull(json);
	}
	
	@Test
	void testNullJSON() {
		JSONObject to = null;
		Account geo = AccountJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Account geo = AccountJSON.fromJSON( to );
		assertNull(geo);
	}

	@Test
	void testFromSupplied() {
		Account to = AonMocker.mock(Account.class);
		JSONObject json = AccountJSON.toJSON(to);
		assertNotNull(json);
		Account supplied = new Account();
		Account from = AccountJSON.fromJSON(json, () -> supplied);
		assertSame(supplied, from);
		AonAsserts.assertClassEquals( to, from);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		Account to = AonMocker.mock(Account.class);
		JSONObject json = AccountJSON.toJSON(to);
		assertNotNull(json);
		Account from = AccountJSON.fromJSON(json);
		AonAsserts.assertClassEquals( to, from);
	}
	
}
