package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


public class InsertTest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = AccountPeriodDAO.getPeriodByYear(ctx,year);
		if (period == null) {
			period = AonFaker.getTodayActiveAccountPeriod( ctx );
			period = AccountPeriodDAO.save(ctx, period);
			assertTrue(AonDateUtils.isSameDay(period.getCreationDate(), new Date()));
			assertEquals(period.getCreationUser(), ctx.getUser());
		}
	}

}
