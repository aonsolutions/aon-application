package com.esferalia.aon.occam.test.accounting.entry;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.test.accounting.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;


public class InsertTest extends AbstractOccamTest {
	
	@Test
	public void testInsert() {
		Date now = new Date();
		AccountPeriod period = ACCOUNTING.fetchPeriod(ctx, now );
		AccountEntry ae = new AccountEntry();
		ae.setDomain(ctx.getDomainId());
		ae.setEntryDate( now );
		ae.setPeriod(period.getId());
		ae.setEntryType( AccountEntryType.MANUAL );
		ae.setConfidential(false);
		String concept = "Nominas";
		ae.addDetail( getAccountEntryDetail("640000000",concept,3151.49,   0.00,null       ,null) );
		ae.addDetail( getAccountEntryDetail("642000000",concept, 617.42,   0.00,"640000000",null) );
		ae.addDetail( getAccountEntryDetail("476000001",concept,0      ,1040.70,"640000000",null) );
		ae.addDetail( getAccountEntryDetail("475100000",concept,0      ,  79.51,"640000000",null) );
		ae.addDetail( getAccountEntryDetail("465000000",concept,0      ,2648.70,"640000000",null) );
		ae = ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, ae);
		
		assertTrue(AonDateUtils.isSameDay(ae.getCreationDate(), new Date()));
		assertEquals(ae.getCreationUser(), ctx.getUser());
	}
	
	private static AccountEntryDetail getAccountEntryDetail(
			String accountCode,
			String concept, 
			double debit,
			double credit, 
			String balancingAccountCode,
			String documentNumber) {
		Account account = ACCOUNTING.getAccount(ctx,accountCode);
		Account balancingAccount = ACCOUNTING.getAccount(ctx,balancingAccountCode);
		return new AccountEntryDetail()
			.setAccount( account.getId() )
			.setAccountCode(account.getCode())
			.setAccountDescription(account.getDescription())
			.setConcept(concept)
			.setDebit(debit)
			.setCredit(credit)
			.setBalancingAccount(balancingAccount == null ? null : balancingAccount.getId())
			.setBalancingAccountCode(balancingAccount == null ? null : balancingAccount.getCode())
			.setBalancingAccountDescription(balancingAccount == null ? null : balancingAccount.getDescription())
			.setDocumentNumber(documentNumber);
	}	
	
}
