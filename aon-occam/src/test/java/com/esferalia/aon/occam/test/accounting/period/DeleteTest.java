package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.test.accounting.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class DeleteTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		int year = AonDateUtils.getYear( new Date() );
		year = year -5;
		AccountPeriod period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(year));
		period.setInitiationDate( AonDateUtils.getYearFirstDay(year) );
		period.setDeadline( AonDateUtils.getYearLastDay(year));
		period.setDomain(ctx.getDomainId());
		period.setStatus(AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.insert(ctx, period);
		
		ACCOUNTING.delete(ctx, period);
		
		period = ACCOUNTING.fetchPeriod(ctx, year);
		assertNull(period);
		
	}

}
