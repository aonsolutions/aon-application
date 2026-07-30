 package com.esferalia.aon.occam.test.accounting.entry;



import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountEntryJSON;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AccountingFaker;

public class AccountEntryJSONTest extends AbstractOccamTest {
	
	@Test
	public void testEmptyJSONObjects() {
		AccountEntry to = AccountingFaker.getAccountEntry(ctx);
		Optional<JSONObject> opt = AccountEntryJSON.toJSON(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		Asserts.assertNotEmptyKeys("AccountEntryJSON", opt.get());
	}
	
	@Test
	public void testNullAccountEntry() {
		AccountEntry to = null;
		Optional<JSONObject> opt = AccountEntryJSON.toJSON( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Optional<AccountEntry> opt = AccountEntryJSON.fromJSON( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Optional<AccountEntry> opt = AccountEntryJSON.fromJSON( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}

	@Test
	public void testFromSupplied() {
		AccountEntry to = AccountingFaker.getAccountEntry(ctx);
		Optional<JSONObject> opt = AccountEntryJSON.toJSON(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		AccountEntry supplied = new AccountEntry();
		Optional<AccountEntry> optFrom = AccountEntryJSON.fromJSON(opt.get(), () -> supplied);
		System.out.println(  opt.get() );
		Asserts.assertAccountEntry( to, optFrom.get());
	}

	@Test
	@Repeat( 20 )
	public void testFromTo() {
		AccountEntry to = AccountingFaker.getAccountEntry(ctx);
		Optional<JSONObject> optJsonTo = AccountEntryJSON.toJSON(to);
		assertNotNull(optJsonTo);
		assertTrue(optJsonTo.isPresent());
		Optional<AccountEntry> optFrom = AccountEntryJSON.fromJSON(optJsonTo.get());
		assertNotNull(optFrom);
		assertTrue(optFrom.isPresent());		
		Asserts.assertAccountEntry( to, optFrom.get());
	}

}
