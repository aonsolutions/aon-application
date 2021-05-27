package com.esferalia.aon.occam.test.registry.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveEmptyScope extends AbstractOccamTest {

	@Test
	public void test() {
		Customer registry = AonFaker.getCustomer( ctx );
		registry.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CustomerDAO.save(ctx, registry) );
		assertEquals(AonError.EMPTY_SCOPE.getMessage(),e.getMessage());
	}

}
