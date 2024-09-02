package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


public class CheckMinDateTest extends AbstractOccamTest {

	@Test
	public void test() {
		LinkedList<AccountPeriod> periods = AccountPeriodDAO.getDomainPeriods(ctx);
		int year = -1;
		if (periods != null) {
			for ( AccountPeriod period : periods) {
				year = AonDateUtils.getYear(period.getInitiationDate());
			}
		}

		year = ( year != -1)? (year-1):AonDateUtils.getYear(new Date()); 
		Date intialDate1 = AonDateUtils.getYearFirstDay(year);
		AccountPeriod period = AccountingFaker.getAccountPeriod(ctx, intialDate1, AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.save(ctx, period);
		Date intialDate2 = AccountPeriodDAO.getMinDate(ctx);
		assertTrue(AonDateUtils.isSameDay(intialDate1,intialDate2));
	}

}
