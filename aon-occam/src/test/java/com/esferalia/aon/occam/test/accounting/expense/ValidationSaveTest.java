package com.esferalia.aon.occam.test.accounting.expense;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingExpenseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingIncomeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveTest extends AbstractOccamTest {

	@Test
	public void emptyDomainTest() {
		AccountingExpense exp = new AccountingExpense();
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}
	
	@Test
	public void emptyDateTest() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_DATE.getMessage(),e.getMessage());
	}
	
	@Test
	public void emptyExpAccount1Test() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_EXP_ACCOUNT.getMessage(),e.getMessage());
	}

	@Test
	public void emptyExpAccount2Test() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		exp.setExpAccount( new Account());
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_EXP_ACCOUNT.getMessage(),e.getMessage());
	}

	@Test
	public void emptyConceptTest() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		exp.setExpAccount( new Account().setId(1));
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_CONCEPT.getMessage(),e.getMessage());
	}

	@Test
	public void emptyAmountTest() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		exp.setExpAccount( new Account().setId(1));
		exp.setConcept("Concepto");
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_AMOUNT.getMessage(),e.getMessage());
	}

	@Test
	public void emptyBankTest() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		exp.setExpAccount( new Account().setId(1));
		exp.setConcept("Concepto");
		exp.setAmount(100.0);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_BANK_ACCOUNT.getMessage(),e.getMessage());
	}
	
	@Test
	public void emptyCreditor() {
		AccountingExpense exp = new AccountingExpense();
		exp.setDomain(DOMAIN_ID);
		exp.setDate( new Date());
		exp.setExpAccount( new Account().setId(1));
		exp.setConcept("Concepto");
		exp.setAmount(100.0);
		exp.setCashAccount( new Account().setId(2));
		exp.setCreditor( new Creditor() );
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountingExpenseDAO.save(ctx, exp) );
		assertEquals(AonError.EMPTY_CUSTOMER.getMessage(),e.getMessage());
	}
}
