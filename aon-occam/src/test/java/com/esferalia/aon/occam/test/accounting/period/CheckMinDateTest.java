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


public class CheckMinDateTest extends AbstractOccamTest {

	@Test
	public void test() {
		Collection<AccountPeriod> periods = AccountPeriodDAO.getDomainPeriods(ctx)
				.collect(Collectors.toCollection(LinkedList::new));
		int year = -1;
		for ( AccountPeriod period : periods) {
			year = AonDateUtils.getYear(period.getInitiationDate());
		}
		year = ( year != -1)? (year-1):AonDateUtils.getYear(new Date()); 
		Date intialDate1 = AonDateUtils.getYearFirstDay(year);
		AccountPeriod period = AonFaker.getAccountPeriod(ctx, intialDate1, AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.save(ctx, period);
		Date intialDate2 = AccountPeriodDAO.getMinDate(ctx);
		assertTrue(AonDateUtils.isSameDay(intialDate1,intialDate2));
	}

}
