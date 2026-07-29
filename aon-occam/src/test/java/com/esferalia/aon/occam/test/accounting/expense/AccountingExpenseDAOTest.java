package com.esferalia.aon.occam.test.accounting.expense;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingExpenseJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingExpenseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class AccountingExpenseDAOTest extends AbstractOccamTest {
	private static final double DELTA = 1e-8;

	@Test
	public void getCashTest() {
		AccountingExpense expense = getCashExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		assertNotNull( ae.getId() );
		Optional<AccountingExpense> optInc = AccountingExpenseDAO.get( ctx, DOMAIN_ID, ae.getId() , null);
		assertNotNull(optInc);
		assertTrue(optInc.isPresent());
		assertTrue(optInc.get().getAccountEntry().isPresent());
		AccountEntry saved = optInc.get().getAccountEntry().get();
		assertTrue(AonCollectionUtils.isNotEmpty(saved.getDetails()));
		assertEquals(2, AonCollectionUtils.size(saved.getDetails()));
		AccountEntryDetail expDetail = saved.getDetails().get(0);
		assertNotNull(expDetail.getAccountId());
		assertNotNull(expDetail.getBalancingAccountId());
		assertEquals(expDetail.getConcept(), expense.getConcept());
		assertEquals(expDetail.getDocumentNumber(), expense.getReferenceCode());
		AccountEntryDetail bankDetail = saved.getDetails().get(1);
		assertNotNull(bankDetail.getAccountId());
		assertNotNull(bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccountId(), bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getBalancingAccountId(), bankDetail.getAccountId());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
		
	}	
	
	@Test
	public void saveCreditorBankTest() {
		AccountingExpense expense = getCustomerExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_EXPENSES, ae.getEntryType());
		assertEquals(expense.getDate(), ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(4, AonCollectionUtils.size(ae.getDetails()));
		
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		AccountEntryDetail custExpDetail = ae.getDetails().get(1);
		AccountEntryDetail custBankDetail = ae.getDetails().get(2);
		AccountEntryDetail bankDetail = ae.getDetails().get(3);
		
		assertEquals(expDetail.getCredit(), custExpDetail.getDebit(), DELTA);
		assertEquals(custBankDetail.getCredit(), custExpDetail.getDebit(), DELTA);
		
		assertEquals(expDetail.getConcept(), expense.getConcept());
		assertEquals(expDetail.getDocumentNumber(), expense.getReferenceCode());
		assertEquals(custExpDetail.getConcept(), expense.getConcept());
		assertEquals(custExpDetail.getDocumentNumber(), expense.getReferenceCode());
		assertEquals(custBankDetail.getConcept(), expense.getConcept());
		assertEquals(custBankDetail.getDocumentNumber(), expense.getReferenceCode());
		assertEquals(bankDetail.getConcept(), expense.getConcept());
		assertEquals(bankDetail.getDocumentNumber(), expense.getReferenceCode());
		
		Creditor creditor = expense.getCreditor()
			.orElseThrow( () -> new AonCoreException("Sin acreedor"));
		assertNotNull(expense.getFinance());
		assertTrue(expense.getFinance().isPresent());
		Finance finance = expense.getFinance().get();
		assertNotNull(finance.getId());
		assertTrue( finance.isPayment() );
		assertTrue(finance.hasRegistry());
		assertEquals( finance.getRegistry().getId(), creditor.getId() );
		assertEquals( finance.getDueDate(), expense.getDate() );
		assertEquals( FinanceStatus.PAID, finance.getFinanceStatus()); 
		assertEquals( finance.getAmount(), expense.getAmount(), DELTA);
		
		FinanceTracking ft = FinanceTrackingDAO.getLastTracking(ctx, finance.getId() );
		assertNotNull(ft);
		assertNotNull(ft.getId());
		assertEquals( FinanceTrackingType.PAID, ft.getType());
		assertEquals( ft.getTrackingDate(), expense.getDate() );
		assertTrue( ft.isRecorded() );
		assertEquals( ft.getAccountEntry(), ae.getId() );
		expense.getAccountEntry().ifPresent( sae -> {
			AccountingExpense saved = AccountingExpenseDAO.get(ctx, DOMAIN_ID, sae.getId(), null).orElse(null);
			System.out.println( AccountingExpenseJSON.to(saved).map( j -> j.toString(2) ).orElse("NULL") );
			
		});
		
	}

	@Test
	public void saveBankTest() {
		AccountingExpense expense = getBankExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_EXPENSES, ae.getEntryType());
		assertEquals(expense.getDate(), ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(ae.getDetails()));
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		assertNotNull(expDetail.getAccountId());
		assertNotNull(expDetail.getBalancingAccountId());
		
		
		AccountEntryDetail bankDetail = ae.getDetails().get(1);
		assertNotNull(bankDetail.getAccountId());
		assertNotNull(bankDetail.getBalancingAccountId());

		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccountId(), bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getBalancingAccountId(), bankDetail.getAccountId());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
	}
	
	@Test
	public void saveCashTest() {
		AccountingExpense expense = getCashExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_EXPENSES, ae.getEntryType());
		assertEquals(expense.getDate(), ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(ae.getDetails()));
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		assertNotNull(expDetail.getAccountId());
		assertNotNull(expDetail.getBalancingAccountId());
		
		
		AccountEntryDetail bankDetail = ae.getDetails().get(1);
		assertNotNull(bankDetail.getAccountId());
		assertNotNull(bankDetail.getBalancingAccountId());

		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccountId(), bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getBalancingAccountId(), bankDetail.getAccountId());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
	}
	
	@Test
	public void deleteCashTest() {
		AccountingExpense expense = getCashExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		AccountingExpenseDAO.delete(ctx, ae );
		
		AccountEntry deleted = AccountEntryDAO.getAccountEntry( ctx, ae.getId() );
		assertNull(deleted);
	}

	@Test
	public void deleteFinanceTest() {
		AccountingExpense expense = getCustomerExpense( );
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		AccountEntry ae = expense.getAccountEntry().get();
		AccountingExpenseDAO.delete(ctx, ae );
		
		AccountEntry deleted = AccountEntryDAO.getAccountEntry( ctx, ae.getId() );
		assertNull(deleted);
		
	}
	
	@Test
	public void updateCashTest() {
		AccountingExpense expense = new AccountingExpense();
		expense.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		expense.setDate( date );
		Account expAccount = AonRandom.getAccountExpense(ctx);
		expense.setExpAccount( expAccount );
		expense.setConcept(AonRandom.string(-1,1,64));
		expense.setReferenceCode( AonRandom.string(32) );
		expense.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		expense.setCashAccount(cashAccount);
		ensureAccountPeriod( expense );
		AccountingExpenseDAO.save(ctx, expense);
		assertNotNull(expense);
		assertNotNull(expense.getAccountEntry());
		assertTrue(expense.getAccountEntry().isPresent());
		
		expense.setAmount (AonMathUtils.round( expense.getAmount() + 10));
		AccountingExpense updated = AccountingExpenseDAO.save( ctx, expense );
		
		assertNotNull(updated);
		assertNotNull(updated.getAccountEntry());
		assertTrue(updated.getAccountEntry().isPresent());
		Optional<AccountingExpense> optSavedUpdate = AccountingExpenseDAO.get( ctx, DOMAIN_ID, updated.getAccountEntry().map( AccountEntry::getId).get(), null);
		assertNotNull(optSavedUpdate);
		assertTrue(optSavedUpdate.isPresent());
		AccountingExpense savedUpdate = optSavedUpdate.get();
		assertNotNull(savedUpdate.getAccountEntry());
		assertTrue(savedUpdate.getAccountEntry().isPresent());
		AccountEntry updatedEntry = AccountEntryDAO.getAccountEntry( ctx, savedUpdate.getAccountEntry().get().getId() );
		assertTrue(AonCollectionUtils.isNotEmpty(updatedEntry.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(updatedEntry.getDetails()));
		AccountEntryDetail expDetail = updatedEntry.getDetails().get(0);
		assertNotNull(expDetail.getAccountId());
		assertNotNull(expDetail.getBalancingAccountId());
		AccountEntryDetail bankDetail = updatedEntry.getDetails().get(1);
		assertNotNull(bankDetail.getAccountId());
		assertNotNull(bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccountId(), bankDetail.getBalancingAccountId());
		assertEquals(expDetail.getBalancingAccountId(), bankDetail.getAccountId());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
		assertEquals(AonMathUtils.absRounded(expDetail.getDebit() - expDetail.getCredit()), savedUpdate.getAmount() , DELTA);
		assertEquals(AonMathUtils.absRounded(bankDetail.getDebit() - bankDetail.getCredit()), savedUpdate.getAmount() , DELTA);
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

	private RegistryBank ensureBanks() {
		Company company = CompanyDAO.getCompany( ctx, DOMAIN_ID);
		return RegistryBankDAO.getStream( ctx, f -> f.getRegistryProperty().eq(company.getId()))
			.findFirst()
			.orElseGet( () -> RegistryBankDAO.save(ctx, 
				new RegistryBank()
					.setDomain( DOMAIN_ID )
					.setRegistry( company.getId() )
					.setBankAccount( new BankAccount("ES5720803616943040032453") )
					.setBic("CAGLESMMXXX") 
					.setSuffix("000")
					.setAlias("BANKOA/Abanca")
					.setActive(true))
			);
	}
	
	private AccountingExpense getCashExpense() {
		AccountingExpense expense = new AccountingExpense();
		expense.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		expense.setDate( date );
		Account expAccount = AonRandom.getAccountExpense(ctx);
		expense.setExpAccount( expAccount );
		expense.setConcept(AonRandom.string(-1,1,64));
		expense.setReferenceCode( AonRandom.string(32) );
		expense.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		expense.setCashAccount(cashAccount);
		return expense;
	}

	private AccountingExpense getBankExpense() {
		RegistryBank rbank = ensureBanks();
		AccountingExpense expense = new AccountingExpense();
		expense.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		expense.setDate( date );
		Account expAccount = AonRandom.getAccountExpense(ctx);
		expense.setExpAccount( expAccount );
		expense.setConcept(AonRandom.string(-1,1,64));
		expense.setReferenceCode( AonRandom.string(32) );
		expense.setAmount(100.0);
		expense.setBank(rbank);
		return expense;
	}
	
	private AccountingExpense getCustomerExpense() {
		RegistryBank rbank = ensureBanks();
		AccountingExpense income = new AccountingExpense();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountExpense(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Creditor creditor = AonRandom.getCreditor(ctx);
		income.setCreditor(creditor);
		income.setBank(rbank);
		return income;
	}
}
