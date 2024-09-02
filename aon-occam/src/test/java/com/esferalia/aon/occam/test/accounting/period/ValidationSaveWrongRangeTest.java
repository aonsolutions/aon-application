package com.esferalia.aon.occam.test.accounting.period;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveWrongRangeTest extends AbstractOccamTest {

	@Test
	public void testPeriodWrongRange() {
		AccountPeriod ap = AccountingFaker.getTodayActiveAccountPeriod( ctx );
		Date start = ap.getInitiationDate();
		Date end = ap.getDeadline(); 
		ap.setInitiationDate(end);
		ap.setDeadline(start);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountPeriodDAO.save(ctx, ap) );
		assertEquals(AonError.ACCOUNT_PERIOD_WRONG_RANGE.getMessage(),e.getMessage());
	}

}
