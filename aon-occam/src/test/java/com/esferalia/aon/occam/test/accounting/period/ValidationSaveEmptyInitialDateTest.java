package com.esferalia.aon.occam.test.accounting.period;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyInitialDateTest extends AbstractOccamTest {

	@Test
	public void test() {
		AccountPeriod ap = AccountingFaker.getTodayActiveAccountPeriod( ctx );
		ap.setInitiationDate(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountPeriodDAO.save(ctx, ap) );
		assertEquals(AonError.ACCOUNT_PERIOD_EMPTY_INITIATION_DATE.getMessage(),e.getMessage());
	}

}
