package com.esferalia.aon.occam.test.accounting.income;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

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
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class AccountingIncomeDAOTest extends AbstractOccamTest {
	private static final double DELTA = 1e-8;

	@Test
	public void saveCustomerBankTest() {
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
		ensureAccountPeriod( income );
		
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(date, ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(ae.getDetails()));
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		assertNotNull(expDetail.getAccount());
		assertNotNull(expDetail.getBalancingAccount());
		assertEquals(expDetail.getConcept(), income.getConcept());
		assertEquals(expDetail.getDocumentNumber(), income.getReferenceCode());
		
		
		AccountEntryDetail bankDetail = ae.getDetails().get(1);
		assertNotNull(bankDetail.getAccount());
		assertNotNull(bankDetail.getBalancingAccount());

		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccount(), bankDetail.getBalancingAccount());
		assertEquals(expDetail.getBalancingAccount(), bankDetail.getAccount());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
		
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
		
	}

	@Test
	public void saveBankTest() {
		RegistryBank rbank = ensureBanks();
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		income.setBank(rbank);
		ensureAccountPeriod( income );
		
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(date, ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(ae.getDetails()));
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		assertNotNull(expDetail.getAccount());
		assertNotNull(expDetail.getBalancingAccount());
		
		
		AccountEntryDetail bankDetail = ae.getDetails().get(1);
		assertNotNull(bankDetail.getAccount());
		assertNotNull(bankDetail.getBalancingAccount());

		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccount(), bankDetail.getBalancingAccount());
		assertEquals(expDetail.getBalancingAccount(), bankDetail.getAccount());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
	}
	
	@Test
	public void saveCashTest() {
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		income.setCashAccount(cashAccount);
		ensureAccountPeriod( income );
		
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		assertNotNull(ae.getId());
		assertEquals(AccountEntryType.OTHER_INCOMES, ae.getEntryType());
		assertEquals(date, ae.getEntryDate());
		assertTrue(AonCollectionUtils.isNotEmpty(ae.getDetails()));
		
		assertEquals(2, AonCollectionUtils.size(ae.getDetails()));
		AccountEntryDetail expDetail = ae.getDetails().get(0);
		assertNotNull(expDetail.getAccount());
		assertNotNull(expDetail.getBalancingAccount());
		
		
		AccountEntryDetail bankDetail = ae.getDetails().get(1);
		assertNotNull(bankDetail.getAccount());
		assertNotNull(bankDetail.getBalancingAccount());

		assertEquals(expDetail.getCredit(), bankDetail.getDebit(), DELTA);
		assertEquals(expDetail.getAccount(), bankDetail.getBalancingAccount());
		assertEquals(expDetail.getBalancingAccount(), bankDetail.getAccount());
		assertEquals(expDetail.getConcept(), bankDetail.getConcept());
		assertEquals(expDetail.getDocumentNumber(), bankDetail.getDocumentNumber());
	}
	
	@Test
	public void deleteCashTest() {
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		income.setCashAccount(cashAccount);
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
		RegistryBank rbank = ensureBanks();
		AccountingIncome income = new AccountingIncome();
		income.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		income.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		income.setExpAccount( expAccount );
		income.setConcept(AonRandom.string(-1,1,64));
		income.setReferenceCode( AonRandom.string(32) );
		income.setAmount(300.0);
		Customer customer = AonRandom.getCustomer(ctx);
		income.setCustomer(customer);
		income.setBank(rbank);
		ensureAccountPeriod( income );
		
		AccountingIncomeDAO.save(ctx, income);
		assertNotNull(income);
		assertNotNull(income.getAccountEntry());
		assertTrue(income.getAccountEntry().isPresent());
		AccountEntry ae = income.getAccountEntry().get();
		System.out.println(ae.getId());
		AccountingIncomeDAO.delete(ctx, ae );
		
		AccountEntry deleted = AccountEntryDAO.getAccountEntry( ctx, ae.getId() );
		assertNull(deleted);
		
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
	

}
