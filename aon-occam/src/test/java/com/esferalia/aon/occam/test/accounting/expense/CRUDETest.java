package com.esferalia.aon.occam.test.accounting.expense;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingExpenseJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingExpenseDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class CRUDETest extends AbstractOccamTest {

	@Test
	public void test1() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		exp.setDate( date );
		Account expAccount = AonRandom.getAccountExpense(ctx);
		exp.setExpAccount( expAccount );
		exp.setConcept(AonRandom.string(-1, 1, 64));
		exp.setReferenceCode( AonRandom.string(32) );
		exp.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		exp.setCashAccount(cashAccount);
		ensureAccountPeriod( exp );
		
		AccountingExpenseDAO.save(ctx, exp);
		assertNotNull(exp);
		assertNotNull(exp.getAccountEntry());
		assertTrue(exp.getAccountEntry().isPresent());
		AccountEntry ae = exp.getAccountEntry().get();
		assertNotNull(ae.getId());
		
		Optional<AccountingExpense> read = AccountingExpenseDAO.get(ctx, DOMAIN_ID, ae.getId(), null);
		assertNotNull(read);
		assertTrue(read.isPresent());
		
		Asserts.assertAccountingExpense( exp, read.get());
		
	}
	
	private void ensureAccountPeriod(AccountingExpense exp) {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, exp.getDate() );
		if (period == null) {
			AccountPeriodDAO.save(ctx, new AccountPeriod()
				.setDomain( DOMAIN_ID )
				.setName( AonNumberUtils.toString( AonDateUtils.getYear( exp.getDate() ) ))
				.setInitiationDate( AonDateUtils.getYearFirstDay( exp.getDate() ))
				.setDeadline( AonDateUtils.getYearLastDay( exp.getDate() ))
				.setStatus( AccountPeriodStatus.OPENING )
			);	
		}
	}

	@Test
	public void streamTest() {
		AccountingExpenseDAO.stream(ctx,DOMAIN_ID,null,0,100, null)
			.forEach( ai -> {
				Optional<JSONObject> json = AccountingExpenseJSON.to( ai );
				assertNotNull(json);
				assertTrue(json.isPresent());
			} );
		
	}
	
}
