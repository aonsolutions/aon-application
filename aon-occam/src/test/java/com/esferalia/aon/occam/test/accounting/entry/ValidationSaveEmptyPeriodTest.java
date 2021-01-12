package com.esferalia.aon.occam.test.accounting.entry;


import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class ValidationSaveEmptyPeriodTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testEmptyPeriod() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);

	}
}
