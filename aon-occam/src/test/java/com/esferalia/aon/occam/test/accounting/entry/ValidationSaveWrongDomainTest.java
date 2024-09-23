package com.esferalia.aon.occam.test.accounting.entry;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class ValidationSaveWrongDomainTest extends AbstractOccamTest {

	@Test
	public void testWrongDomain() {
		AccountPeriod period = ACCOUNTING.getPeriodByYear(ctx,1974);
		if (period == null) {
			period = new AccountPeriod();
			period.setName("1974");
			period.setInitiationDate( AonDateUtils.getDate(1974, 0, 1));
			period.setDeadline( AonDateUtils.getDate(1974, 11, 31));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			ACCOUNTING.save(ctx, period);
		}
		AccountEntry accountEntry = new AccountEntry();
		accountEntry.setDomain(100); // Other
		accountEntry.setEntryDate( AonDateUtils.getDate(1974, 5, 4) );
		accountEntry.setPeriod(period.getId());
		assertThrows(AonCoreException.class, () ->
			ACCOUNTING.save(getOccam(), accountEntry));
	}
	
}
