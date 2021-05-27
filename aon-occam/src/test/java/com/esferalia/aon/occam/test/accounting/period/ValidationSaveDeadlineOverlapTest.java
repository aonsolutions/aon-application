package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class ValidationSaveDeadlineOverlapTest extends AbstractOccamTest {

	@Test
	public void testPeriodDeadlineOverlap() {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO.getDomainPeriods(ctx);
		int year = -1;
		for ( AccountPeriod period : periods) {
			year = AonDateUtils.getYear(period.getInitiationDate());
		}
		year = ( year != -1)? (year-1):AonDateUtils.getYear(new Date());
		AccountPeriod period = AccountPeriodDAO.getPeriodByYear(ctx,year);
		if (period == null) {
			period = AonFaker.getAccountPeriod( ctx, AonDateUtils.getYearFirstDay(year), AccountPeriodStatus.ACTIVE );
			AccountPeriodDAO.save(ctx, period);
		}
		int previousYear = year - 1;		
		AccountPeriod nextPeriod = new AccountPeriod()
			.setDomain(ctx.getDomainId())
			.setName(AonNumberUtils.toString(previousYear))
			.setInitiationDate( AonDateUtils.getDate(previousYear, 5, 1))
			.setDeadline( AonDateUtils.getDate(year, 4, 31))
			.setStatus( AccountPeriodStatus.ACTIVE );
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountPeriodDAO.save(ctx, nextPeriod) );
		assertEquals(AonError.ACCOUNT_PERIOD_END_OVERLAP.format(period.getName()),e.getMessage());
	}

}
