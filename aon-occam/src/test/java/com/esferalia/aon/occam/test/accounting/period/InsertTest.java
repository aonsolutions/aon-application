package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class InsertTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx, year);
		if (period == null) {
			period = new AccountPeriod();
			period.setName(AonNumberUtils.toString(year));
			period.setInitiationDate( AonDateUtils.getYearFirstDay(year) );
			period.setDeadline( AonDateUtils.getYearLastDay(year));
			period.setDomain(ctx.getDomainId());
			period.setStatus(AccountPeriodStatus.ACTIVE);
			period = ACCOUNTING.insert(ctx, period);
			assertTrue(AonDateUtils.isSameDay(period.getCreationDate(), new Date()));
			assertEquals(period.getCreationUser(), ctx.getUser());
		} else {
			period.setInitiationDate( AonDateUtils.getYearFirstDay(year) );
			period.setDeadline( AonDateUtils.getYearLastDay(year));
			ACCOUNTING.update(ctx, period);
			period = ACCOUNTING.fetchPeriod(ctx, period.getId());	
			assertTrue(AonDateUtils.isSameDay(period.getModificationDate(), new Date()));
			assertEquals(period.getModificationUser(), ctx.getUser());
		}
	}

}
