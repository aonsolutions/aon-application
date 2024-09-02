package com.esferalia.aon.occam.test.accounting.period;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


public class UpdateTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period = AccountPeriodDAO.getPeriodByYear(ctx,year);
		if (period == null) {
			period = AccountingFaker.getTodayActiveAccountPeriod( ctx );
			period = AccountPeriodDAO.save(ctx, period);
			assertTrue(AonDateUtils.isSameDay(period.getCreationDate(), new Date()));
			assertEquals(period.getCreationUser(), ctx.getUser());
		}
		
		period = AccountPeriodDAO.getPeriodByYear(ctx,year);
		if (period == null) {
			throw new IllegalStateException("No encontrado");
		}
		period.setStatus(AccountPeriodStatus.INACTIVE);
		AccountPeriodDAO.save(ctx, period);
		period = AccountPeriodDAO.getPeriod(ctx,period.getId());
		assertEquals(period.getStatus(), AccountPeriodStatus.INACTIVE);
		assertTrue(AonDateUtils.isSameDay(period.getModificationDate(), new Date()));
		assertEquals(period.getModificationUser(), ctx.getUser());
		
		period.setStatus(AccountPeriodStatus.ACTIVE);
		AccountPeriodDAO.save(ctx, period);
		period = AccountPeriodDAO.getPeriod(ctx,period.getId());
		assertEquals(period.getStatus(), AccountPeriodStatus.ACTIVE);
		assertTrue(AonDateUtils.isSameDay(period.getModificationDate(), new Date()));
		assertEquals(period.getModificationUser(), ctx.getUser());
	}

}
