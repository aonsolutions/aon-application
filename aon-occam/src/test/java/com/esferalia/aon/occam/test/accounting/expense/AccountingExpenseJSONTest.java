 package com.esferalia.aon.occam.test.accounting.expense;



import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingExpenseJSON;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AccountingFaker;

public class AccountingExpenseJSONTest extends AbstractOccamTest {
	
	@Test
	public void testEmptyJSONObjects() {
		AccountingExpense to = AccountingFaker.getAccountingExpense(ctx);
		Optional<JSONObject> opt = AccountingExpenseJSON.to(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		Asserts.assertNotEmptyKeys("AccountingExpenseJSON", opt.get());
	}
	
	@Test
	public void testNullAccountingExpense() {
		AccountingExpense to = null;
		Optional<JSONObject> opt = AccountingExpenseJSON.to( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Optional<AccountingExpense> opt = AccountingExpenseJSON.from( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}
	
	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Optional<AccountingExpense> opt = AccountingExpenseJSON.from( to );
		assertNotNull(opt);
		assertTrue(opt.isEmpty());
	}

	@Test
	public void testFromSupplied() {
		AccountingExpense to = AccountingFaker.getAccountingExpense(ctx);
		Optional<JSONObject> opt = AccountingExpenseJSON.to(to);
		assertNotNull(opt);
		assertTrue(opt.isPresent());
		
		AccountingExpense supplied = new AccountingExpense();
		Optional<AccountingExpense> optFrom = AccountingExpenseJSON.from(opt.get(), () -> supplied);
		Asserts.assertAccountingExpense( to, optFrom.get());
	}

	@Test
//	@Repeat( 20 )
	public void testFromTo() {
		AccountingExpense to = AccountingFaker.getAccountingExpense(ctx);
		Optional<JSONObject> optJsonTo = AccountingExpenseJSON.to(to);
		assertNotNull(optJsonTo);
		assertTrue(optJsonTo.isPresent());
		Optional<AccountingExpense> optFrom = AccountingExpenseJSON.from(optJsonTo.get());
		assertNotNull(optFrom);
		assertTrue(optFrom.isPresent());		
		Asserts.assertAccountingExpense( to, optFrom.get());
	}
	
}
