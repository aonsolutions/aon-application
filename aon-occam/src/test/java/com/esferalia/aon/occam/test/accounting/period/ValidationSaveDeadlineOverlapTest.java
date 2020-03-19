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


public class ValidationSaveDeadlineOverlapTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testPeriodDeadlineOverlap() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = ACCOUNTING.fetchPeriodByYear(ctx,year);
		if (period == null) {
			period = new AccountPeriod();
			period.setName(AonNumberUtils.toString(year));
			period.setInitiationDate( AonDateUtils.getYearFirstDay(year) );
			period.setDeadline( AonDateUtils.getYearLastDay(year));
			period.setStatus( AccountPeriodStatus.ACTIVE );
			period.setDomain(ctx.getDomainId());
			ACCOUNTING.insert(ctx, period);
		}
		int previousYear = year - 1;		
		period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(previousYear));
		period.setInitiationDate( AonDateUtils.getDate(previousYear, 5, 1));
		period.setDeadline( AonDateUtils.getDate(year, 4, 31));
		period.setStatus( AccountPeriodStatus.ACTIVE );
		period.setDomain(ctx.getDomainId());
		ACCOUNTING.insert(ctx, period);
	}

}
