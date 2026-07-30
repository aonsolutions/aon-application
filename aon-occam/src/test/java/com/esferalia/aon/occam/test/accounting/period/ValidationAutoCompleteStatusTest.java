package com.esferalia.aon.occam.test.accounting.period;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


public class ValidationAutoCompleteStatusTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = 1000;
		AccountPeriod ap = AccountPeriodDAO.getPeriodByYear(ctx, year);
		if (ap != null) {
			AccountPeriodDAO.delete(ctx, ap);
		}
		
		Date periodDate = AonDateUtils.getDate(year, 1, 1);
		ap = AccountingFaker.getAccountPeriod(ctx , periodDate, null);
		ap.setStatus(null);
		ap = AccountPeriodDAO.save(ctx, ap);
		assertEquals(ap.getStatus(),AccountPeriodStatus.ACTIVE);
		
		
	}
}
