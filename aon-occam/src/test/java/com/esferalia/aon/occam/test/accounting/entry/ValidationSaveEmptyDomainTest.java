package com.esferalia.aon.occam.test.accounting.entry;


import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testEmptyDomain() {
		AccountEntry accountEntry = new AccountEntry();
		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry);
	}
	
}
