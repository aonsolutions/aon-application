package com.esferalia.aon.occam.test.registry.customer;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test @Ignore
	public void test() {
		Customer registry = AonFaker.getCustomer( ctx );
		registry.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CustomerDAO.save(ctx, registry) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
