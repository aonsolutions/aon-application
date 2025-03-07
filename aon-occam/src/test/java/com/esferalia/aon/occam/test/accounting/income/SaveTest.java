package com.esferalia.aon.occam.test.accounting.income;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingIncomeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class SaveTest extends AbstractOccamTest {
	private static final double DELTA = 1e-8;

	@Test
	@Ignore
	public void saveCustomerBankTest() {
		RegistryBank rbank = ensureBanks();
		AccountingIncome exp = new AccountingIncome();
		exp.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		exp.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		exp.setExpAccount( expAccount );
		exp.setConcept(AonRandom.string(64));
		exp.setReferenceCode( AonRandom.string(32) );
		exp.setAmount(100.0);
		exp.setCustomer(AonRandom.getCustomer(ctx));
		exp.setBank(rbank);
		ensureAccountPeriod( exp );
		
		AccountingIncomeDAO.save(ctx, exp);
		assertNotNull(exp);
		assertNotNull(exp.getAccountEntry());
		assertTrue(exp.getAccountEntry().isPresent());
		AccountEntry ae = exp.getAccountEntry().get();
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
	public void saveBankTest() {
		RegistryBank rbank = ensureBanks();
		AccountingIncome exp = new AccountingIncome();
		exp.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		exp.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		exp.setExpAccount( expAccount );
		exp.setConcept(AonRandom.string(64));
		exp.setReferenceCode( AonRandom.string(32) );
		exp.setAmount(100.0);
		exp.setBank(rbank);
		ensureAccountPeriod( exp );
		
		AccountingIncomeDAO.save(ctx, exp);
		assertNotNull(exp);
		assertNotNull(exp.getAccountEntry());
		assertTrue(exp.getAccountEntry().isPresent());
		AccountEntry ae = exp.getAccountEntry().get();
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
		AccountingIncome exp = new AccountingIncome();
		exp.setDomain(DOMAIN_ID);
		Date date = AonRandom.getPastDate(-1);
		exp.setDate( date );
		Account expAccount = AonRandom.getAccountIncome(ctx);
		exp.setExpAccount( expAccount );
		exp.setConcept(AonRandom.string(64));
		exp.setReferenceCode( AonRandom.string(32) );
		exp.setAmount(100.0);
		Account cashAccount = AonRandom.getAccountCash(ctx);
		exp.setCashAccount(cashAccount);
		ensureAccountPeriod( exp );
		
		AccountingIncomeDAO.save(ctx, exp);
		assertNotNull(exp);
		assertNotNull(exp.getAccountEntry());
		assertTrue(exp.getAccountEntry().isPresent());
		AccountEntry ae = exp.getAccountEntry().get();
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
