package com.esferalia.aon.occam.test.accounting.entry;


import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDateTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testEmptyDate() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
}
