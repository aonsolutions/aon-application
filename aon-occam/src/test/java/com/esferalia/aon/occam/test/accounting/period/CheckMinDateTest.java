package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;


public class CheckMinDateTest extends AbstractOccamTest {

	@Test
	public void testCheckMinDate() {
		int year = 1000;
		Date intialDate1 = AonDateUtils.getYearFirstDay(year);
		AccountPeriod period = new AccountPeriod();
		period.setName(AonNumberUtils.toString(year));
		period.setInitiationDate( intialDate1 );
		period.setDeadline( AonDateUtils.getYearLastDay(year));
		period.setDomain(ctx.getDomainId());
		period.setStatus(AccountPeriodStatus.ACTIVE);
		period = ACCOUNTING.insert(ctx, period);
		
		Date intialDate2 = AccountPeriodDAO.getMinDate(ctx);
		
		assertTrue(AonDateUtils.isSameDay(intialDate1,intialDate2));
		
	}

}
