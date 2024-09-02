package com.esferalia.aon.occam.test.accounting.entry;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDateTest extends AbstractOccamTest {

	@Test
	public void testEmptyDate() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		assertThrows(AonCoreException.class, () ->
			ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, accountEntry));
	}
	
}
