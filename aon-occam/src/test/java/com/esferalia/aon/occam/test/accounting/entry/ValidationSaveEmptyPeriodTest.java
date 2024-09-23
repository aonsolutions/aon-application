package com.esferalia.aon.occam.test.accounting.entry;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class ValidationSaveEmptyPeriodTest extends AbstractOccamTest {

	@Test
	public void testEmptyPeriod() {
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(ctx.getDomainId());
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		assertThrows(AonCoreException.class, () ->
			ACCOUNTING.save(getOccam(), accountEntry));

	}
}
