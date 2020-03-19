package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.jooq.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class CheckMaxDateTest extends AbstractOccamTest {

	@Test
	public void testCheckMaxDate() {
		int year = 9999;
		Date lastDate1 = AonDateUtils.getYearLastDay(year);
		AccountPeriod period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(year));
		period.setInitiationDate( AonDateUtils.getYearFirstDay(year) );
		period.setDeadline( lastDate1 );
		period.setDomain(ctx.getDomainId());
		period.setStatus(AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.insert(ctx, period);
		
		Date lastDate2 = AccountPeriodDAO.getMaxDate(ctx);
		
		assertTrue(AonDateUtils.isSameDay(lastDate1,lastDate2));
		
	}

}
