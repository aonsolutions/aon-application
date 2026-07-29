package com.esferalia.aon.occam.test.accounting.period;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class ValidationSaveInitialDateOverlapTest extends AbstractOccamTest {

	@Test
	public void test() {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO.getDomainPeriods(ctx);
		int year = -1;
		for ( AccountPeriod period : periods) {
			year = AonDateUtils.getYear(period.getInitiationDate());
			break;
		}
		year = ( year != -1)? (year+1):AonDateUtils.getYear(new Date()); 
		AccountPeriod period = AccountPeriodDAO.getPeriodByYear(ctx,year);
		if (period == null) {
			period = AccountingFaker.getAccountPeriod( ctx, AonDateUtils.getYearFirstDay(year), AccountPeriodStatus.ACTIVE );
			AccountPeriodDAO.save(ctx, period);
		}
		int nextYear = year + 1;
		AccountPeriod nextPeriod = new AccountPeriod()
			.setDomain(ctx.getDomainId())
			.setName(AonNumberUtils.toString(nextYear))
			.setInitiationDate( AonDateUtils.getDate(year, 5, 1))
			.setDeadline( AonDateUtils.getDate(nextYear, 4, 31))
			.setStatus( AccountPeriodStatus.ACTIVE );
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountPeriodDAO.save(ctx, nextPeriod) );
		assertEquals(AonError.ACCOUNT_PERIOD_START_OVERLAP.format(period.getName()),e.getMessage());
	}
}
