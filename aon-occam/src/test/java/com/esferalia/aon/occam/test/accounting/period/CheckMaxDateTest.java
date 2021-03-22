package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


public class CheckMaxDateTest extends AbstractOccamTest {

	@Test
	public void test() {
		Collection<AccountPeriod> periods = AccountPeriodDAO.getDomainPeriods(ctx)
				.collect(Collectors.toCollection(LinkedList::new));
		int year = -1;
		for ( AccountPeriod period : periods) {
			year = AonDateUtils.getYear(period.getInitiationDate());
			break;
		}
		year = ( year != -1)? (year+1):AonDateUtils.getYear(new Date()); 
		Date lastDate1 = AonDateUtils.getYearLastDay(year);
		AccountPeriod period = AonFaker.getAccountPeriod(ctx, lastDate1, AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.save(ctx, period);
		Date lastDate2 = AccountPeriodDAO.getMaxDate(ctx);
		assertTrue(AonDateUtils.isSameDay(lastDate1,lastDate2));
		
	}

}
