package com.esferalia.aon.occam.test.accounting.period;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test
	public void test() {
		AccountPeriod ap = AccountingFaker.getTodayActiveAccountPeriod( ctx );
		ap.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AccountPeriodDAO.save(ctx, ap) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
	}
}
