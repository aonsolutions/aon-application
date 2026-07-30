 package com.esferalia.aon.occam.test.accounting.income;



import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingIncomeJSON;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AccountingFaker;

public class AccountingIncomeJSONTest extends AbstractOccamTest {
	
	@Test
	public void testEmptyJSONObjects() {
		AccountingIncome to = AccountingFaker.getAccountingIncome(ctx);
		Optional<JSONObject> opt = AccountingIncomeJSON.to(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		Asserts.assertNotEmptyKeys("AccountingIncomeJSON", opt.get());
	}
	
	@Test
	public void testNullAccountingIncome() {
		AccountingIncome to = null;
		Optional<JSONObject> opt = AccountingIncomeJSON.to( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Optional<AccountingIncome> opt = AccountingIncomeJSON.from( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Optional<AccountingIncome> opt = AccountingIncomeJSON.from( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}

	@Test
	public void testFromSupplied() {
		AccountingIncome to = AccountingFaker.getAccountingIncome(ctx);
		Optional<JSONObject> opt = AccountingIncomeJSON.to(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		
		AccountingIncome supplied = new AccountingIncome();
		Optional<AccountingIncome> optFrom = AccountingIncomeJSON.from(opt.get(), () -> supplied);
		Asserts.assertAccountingIncome( to, optFrom.get());
	}

	@Test
//	@Repeat( 20 )
	public void testFromTo() {
		AccountingIncome to = AccountingFaker.getAccountingIncome(ctx);
		Optional<JSONObject> optJsonTo = AccountingIncomeJSON.to(to);
		assertNotNull(optJsonTo);
		assertTrue(optJsonTo.isPresent());
		Optional<AccountingIncome> optFrom = AccountingIncomeJSON.from(optJsonTo.get());
		assertNotNull(optFrom);
		assertTrue(optFrom.isPresent());		
		Asserts.assertAccountingIncome( to, optFrom.get());
	}
	
}
