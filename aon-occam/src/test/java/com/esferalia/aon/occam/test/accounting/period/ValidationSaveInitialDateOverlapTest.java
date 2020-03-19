package com.esferalia.aon.occam.test.accounting.period;


import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class ValidationSaveInitialDateOverlapTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testPeriodInitialDateOverlap() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx,year);
		if (period == null) {
			period = new AccountPeriod();
			period.setName(AonNumberUtils.toString(year));
			period.setInitiationDate( AonDateUtils.getDate(year, 0, 1));
			period.setDeadline( AonDateUtils.getDate(year, 11, 31));
			period.setDomain(ctx.getDomainId());
			period.setStatus( AccountPeriodStatus.ACTIVE );
			ACCOUNTING.insert( ctx , period);
		}
		int nextYear = year + 1;
		period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(nextYear));
		period.setInitiationDate( AonDateUtils.getDate(year, 5, 1));
		period.setDeadline( AonDateUtils.getDate(nextYear, 4, 31));
		period.setStatus( AccountPeriodStatus.ACTIVE );
		period.setDomain(ctx.getDomainId());
		ACCOUNTING.insert(ctx, period);
	}
}
