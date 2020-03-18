package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.test.accounting.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;


public class UpdateTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx,year);
		if (period == null) {
			throw new IllegalStateException("No encontrado");
		}
		period.setStatus(AccountPeriodStatus.INACTIVE);
		ACCOUNTING.update(ctx, period);
		period = ACCOUNTING.fetchPeriod(ctx, period.getId());
		assertTrue(AonDateUtils.isSameDay(period.getModificationDate(), new Date()));
		assertEquals(period.getModificationUser(), ctx.getUser());

		period.setStatus(AccountPeriodStatus.ACTIVE);
		ACCOUNTING.update(ctx, period);
		period = ACCOUNTING.fetchPeriod(ctx, period.getId());
		assertTrue(AonDateUtils.isSameDay(period.getModificationDate(), new Date()));
		assertEquals(period.getModificationUser(), ctx.getUser());
		
	}

}
