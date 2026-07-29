package com.esferalia.aon.occam.test.accounting.income;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertFalse;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.json.AccountingIncomeJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingIncomeDAO;
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


public class AccountingIncomeDAOTest extends AbstractOccamTest {
	private static final double DELTA = 1e-8;

	@Test
	public void getCashTest() {
		AccountingIncome income = getCashIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull( ae.getId() );
		Optional<AccountingIncome> optInc = AccountingIncomeDAO.get( ctx, DOMAIN_ID, ae.getId() , null);
		assertNotNull(optInc);
		assertTrue(optInc.isPresent());
		assertTrue(optInc.get().getAccountEntry().isPresent());
		AccountEntry saved = optInc.get().getAccountEntry().get();
		assertTrue(AonCollectionUtils.isNotEmpty(saved.getDetails()));
		assertEquals(2, AonCollectionUtils.size(saved.getDetails()));
		AccountEntryDetail expDetail = saved.getDetails().get(0);
		assertNotNull(expDetail.getAccountId());
		assertNotNull(expDetail.getBalancingAccountId());
		assertEquals(expDetail.getConcept(), income.getConcept());
		assertEquals(expDetail.getDocumentNumber(), income.getReferenceCode());
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
	public void saveCustomerBankTest() {
		AccountingIncome income = getCustomerIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(income.getDate(), ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(4, AonCollectionUtils.size(ae.getDetails()));
		
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		AccountEntryDetail custExpDetail = ae.getDetails().get(1);
		AccountEntryDetail custBankDetail = ae.getDetails().get(2);
		AccountEntryDetail bankDetail = ae.getDetails().get(3);
		
		assertEquals(expDetail.getCredit(), custExpDetail.getDebit(), DELTA);
		assertEquals(custBankDetail.getCredit(), custExpDetail.getDebit(), DELTA);
		
		assertEquals(expDetail.getConcept(), income.getConcept());
		assertEquals(expDetail.getDocumentNumber(), income.getReferenceCode());
		assertEquals(custExpDetail.getConcept(), income.getConcept());
		assertEquals(custExpDetail.getDocumentNumber(), income.getReferenceCode());
		assertEquals(custBankDetail.getConcept(), income.getConcept());
		assertEquals(custBankDetail.getDocumentNumber(), income.getReferenceCode());
		assertEquals(bankDetail.getConcept(), income.getConcept());
		assertEquals(bankDetail.getDocumentNumber(), income.getReferenceCode());
		
		Customer customer = income.getCustomer()
			.orElseThrow( () -> new AonCoreException("Sin cliente"));
		assertNotNull(income.getFinance());
		assertTrue(income.getFinance().isPresent());
		Finance finance = income.getFinance().get();
		assertNotNull(finance.getId());
		assertFalse( finance.isPayment() );
		assertTrue(finance.hasRegistry());
		assertEquals( finance.getRegistry().getId(), customer.getId() );
		assertEquals( finance.getDueDate(), income.getDate() );
		assertEquals( FinanceStatus.PAID, finance.getFinanceStatus()); 
		assertEquals( finance.getAmount(), income.getAmount(), DELTA);
		
		FinanceTracking ft = FinanceTrackingDAO.getLastTracking(ctx, finance.getId() );
		assertNotNull(ft);
		assertNotNull(ft.getId());
		assertEquals( FinanceTrackingType.PAID, ft.getType());
		assertEquals( ft.getTrackingDate(), income.getDate() );
		assertTrue( ft.isRecorded() );
		assertEquals( ft.getAccountEntry(), ae.getId() );
		income.getAccountEntry().ifPresent( sae -> {
			AccountingIncome saved = AccountingIncomeDAO.get(ctx, DOMAIN_ID, sae.getId(), null).orElse(null);
			System.out.println( AccountingIncomeJSON.to(saved).map( j -> j.toString(2) ).orElse("NULL") );
			
		});
	}

	@Test
	public void saveBankTest() {
		AccountingIncome income = getBankIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(income.getDate(), ae.getEntryDate());
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
		AccountingIncome income = getCashIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(income.getDate(), ae.getEntryDate());
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
		AccountingIncome income = getCashIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		AccountingIncomeDAO.delete(ctx, ae );
		
		AccountEntry deleted = AccountEntryDAO.getAccountEntry( ctx, ae.getId() );
		assertNull(deleted);
	}

	@Test
	public void deleteFinanceTest() {
		AccountingIncome income = getCustomerIncome( );
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		AccountingIncomeDAO.delete(ctx, ae );
		
		AccountEntry deleted = AccountEntryDAO.getAccountEntry( ctx, ae.getId() );
		assertNull(deleted);
		
	}
	
	@Test
	public void updateCashTest() {
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		income.setCashAccount(cashAccount);
		ensureAccountPeriod( income );
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		//AccountEntry ae = income.getAccountEntry().get();
		
		income.setAmount (AonMathUtils.round( income.getAmount() + 10));
		AccountingIncome updated = AccountingIncomeDAO.save( ctx, income );
		
		assertNotNull(updated);
		assertNotNull(updated.getAccountEntry());
		assertTrue(updated.getAccountEntry().isPresent());
		Optional<AccountingIncome> optSavedUpdate = AccountingIncomeDAO.get( ctx, DOMAIN_ID, updated.getAccountEntry().map( AccountEntry::getId).get(), null);
		assertNotNull(optSavedUpdate);
		assertTrue(optSavedUpdate.isPresent());
		AccountingIncome savedUpdate = optSavedUpdate.get();
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
		assertEquals(AonMathUtils.absRounded(expDetail.getCredit() - expDetail.getDebit()), savedUpdate.getAmount() , DELTA);
		assertEquals(AonMathUtils.absRounded(bankDetail.getCredit() - bankDetail.getDebit()), savedUpdate.getAmount() , DELTA);
	}

	private void ensureAccountPeriod(AccountingIncome exp) {
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
	
	private AccountingIncome getCashIncome() {
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		income.setCashAccount(cashAccount);
		return income;
	}

	private AccountingIncome getBankIncome() {
		RegistryBank rbank = ensureBanks();
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		income.setBank(rbank);
		return income;
	}
	
	private AccountingIncome getCustomerIncome() {
		RegistryBank rbank = ensureBanks();
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Customer customer = AonRandom.getCustomer(ctx);
		income.setCustomer(customer);
		income.setBank(rbank);
		return income;
	}
}
