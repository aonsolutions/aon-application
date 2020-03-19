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


public class ValidationSaveWrongRangeTest extends AbstractOccamTest {

	@Test(expected=AonCoreException.class)
	public void testPeriodWrongRange() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(year));
		period.setInitiationDate( AonDateUtils.getYearLastDay(year));
		period.setDeadline( AonDateUtils.getYearFirstDay(year) );
		period.setStatus( AccountPeriodStatus.ACTIVE );
		period.setDomain(ctx.getDomainId());
		ACCOUNTING.insert(ctx, period);
	}

}
