package com.esferalia.aon.occam.test.accounting.period;


import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;


public class DeleteTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = AonDateUtils.getYear( new Date() );
		AccountPeriod period =  null;
		while (period ==  null) {
			year = year - 1;
			period = AccountPeriodDAO.getPeriodByYear(ctx, year);
		} 
		AccountPeriodDAO.delete(ctx, period);
		period = ACCOUNTING.getPeriod(ctx, year);
		assertNull(period);
	}

}
